package com.drdisagree.teledrive.presentation.files

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drdisagree.teledrive.core.telegram.RemoteDocument
import com.drdisagree.teledrive.domain.model.TelegramImportChat
import kotlinx.coroutines.launch

@Composable
internal fun TelegramImportDialog(
    viewModel: FilesViewModel,
    onChooseDestination: (List<RemoteDocument>, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var chats by remember { mutableStateOf<List<TelegramImportChat>>(emptyList()) }
    var selectedChat by remember { mutableStateOf<TelegramImportChat?>(null) }
    var documents by remember { mutableStateOf<List<RemoteDocument>>(emptyList()) }
    var selected by remember { mutableStateOf<Set<RemoteDocument>>(emptySet()) }
    var nextFromMessageId by remember { mutableStateOf(0L) }
    var loading by remember { mutableStateOf(true) }
    var loadingMore by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var confirmEncryptionWarning by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        runCatching { viewModel.listTelegramImportChats() }
            .onSuccess { chats = it }
            .onFailure { error = it.message ?: "Não foi possível listar as conversas do Telegram." }
        loading = false
    }

    fun loadPage(chat: TelegramImportChat, cursor: Long) {
        scope.launch {
            if (cursor == 0L) loading = true else loadingMore = true
            error = null
            runCatching { viewModel.telegramImportDocuments(chat.chatId, cursor) }
                .onSuccess { page ->
                    documents = if (cursor == 0L) page.documents else documents + page.documents
                    nextFromMessageId = page.nextFromMessageId
                }
                .onFailure { error = it.message ?: "Não foi possível carregar arquivos desta conversa." }
            loading = false
            loadingMore = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(selectedChat?.title ?: "Importar do Telegram") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (selectedChat == null) {
                    Text("Escolha um grupo ou canal acessível à sua conta.")
                    if (loading) CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                    LazyColumn(Modifier.heightIn(max = 360.dp)) {
                        items(chats, key = { it.chatId }) { chat ->
                            Row(
                                Modifier.fillMaxWidth().clickable { selectedChat = chat; loadPage(chat, 0) }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(chat.title, modifier = Modifier.weight(1f))
                                Text(if (chat.isChannel) "Canal" else "Grupo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    if (!loading && chats.isEmpty() && error == null) Text("Nenhum grupo ou canal disponível.")
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = {
                            selectedChat = null
                            documents = emptyList()
                            selected = emptySet()
                            nextFromMessageId = 0L
                            loading = false
                            loadingMore = false
                            error = null
                        }) { Text("Trocar conversa") }
                        Text("${selected.size} selecionado(s)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.weight(1f))
                        val allLoadedSelected = documents.isNotEmpty() && documents.all { it in selected }
                        TextButton(
                            enabled = documents.isNotEmpty() && !loading,
                            onClick = {
                                selected = if (allLoadedSelected) {
                                    selected - documents.toSet()
                                } else {
                                    selected + documents
                                }
                            }
                        ) { Text(if (allLoadedSelected) "Limpar seleção" else "Selecionar tudo") }
                    }
                    if (loading) CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                    LazyColumn(Modifier.heightIn(max = 340.dp)) {
                        items(documents, key = { "${it.chatId}:${it.messageId}" }) { document ->
                            val checked = document in selected
                            Row(
                                Modifier.fillMaxWidth().clickable {
                                    selected = if (checked) selected - document else selected + document
                                }.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked, onCheckedChange = { value ->
                                    selected = if (value) selected + document else selected - document
                                })
                                Column(Modifier.weight(1f).padding(start = 4.dp)) {
                                    Text(document.fileName, maxLines = 2)
                                    Text(
                                        "${document.mimeType} · ${formatImportSize(document.sizeBytes)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    if (!loading && documents.isEmpty() && error == null) Text("Nenhum documento compatível encontrado.")
                    if (nextFromMessageId != 0L) {
                        TextButton(
                            enabled = !loadingMore,
                            onClick = { selectedChat?.let { loadPage(it, nextFromMessageId) } }
                        ) { Text(if (loadingMore) "Carregando…" else "Carregar mais") }
                    }
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            if (selectedChat != null) {
                Button(enabled = selected.isNotEmpty() && !loading, onClick = {
                    scope.launch {
                        if (viewModel.remoteImportNeedsEncryptionWarning()) confirmEncryptionWarning = true
                        else onChooseDestination(selected.toList(), false)
                    }
                }) { Text("Escolher pasta (${selected.size})") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )

    if (confirmEncryptionWarning) {
        AlertDialog(
            onDismissRequest = { confirmEncryptionWarning = false },
            title = { Text("Cópia sem criptografia do TeleDrive") },
            text = {
                Text("O Telegram copiará os arquivos diretamente no servidor. Esta cópia não será criptografada pelo TeleDrive e ficará marcada como não criptografada. Deseja continuar?")
            },
            confirmButton = {
                Button(onClick = {
                    confirmEncryptionWarning = false
                    onChooseDestination(selected.toList(), true)
                }) { Text("Continuar sem criptografia") }
            },
            dismissButton = { TextButton(onClick = { confirmEncryptionWarning = false }) { Text("Voltar") } }
        )
    }
}

private fun formatImportSize(size: Long): String = when {
    size < 1_000L -> "$size B"
    size < 1_000_000L -> "${size / 1_000} KB"
    size < 1_000_000_000L -> "${size / 1_000_000} MB"
    else -> "${size / 1_000_000_000} GB"
}
