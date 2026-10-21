package com.drdisagree.teledrive.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.info_contents
import com.drdisagree.teledrive.resources.info_created
import com.drdisagree.teledrive.resources.info_favorite
import com.drdisagree.teledrive.resources.info_modified
import com.drdisagree.teledrive.resources.info_no
import com.drdisagree.teledrive.resources.info_yes
import com.drdisagree.teledrive.domain.model.DriveFolder
import com.drdisagree.teledrive.presentation.common.Formatters
import com.drdisagree.teledrive.resources.folder_color_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderInfoSheet(
    folder: DriveFolder,
    colorKey: String?,
    onColorSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(text = folder.name, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            FolderInfoRow(
                label = stringResource(Res.string.info_contents),
                value = folder.contentsLabel()
            )

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            FolderInfoRow(
                label = stringResource(Res.string.info_created),
                value = Formatters.dateTime(folder.createdAt)
            )
            FolderInfoRow(
                label = stringResource(Res.string.info_modified),
                value = Formatters.dateTime(folder.modifiedAt)
            )
            FolderInfoRow(
                label = stringResource(Res.string.info_favorite),
                value = stringResource(
                    if (folder.isFavorite) Res.string.info_yes else Res.string.info_no
                )
            )

            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text(
                text = stringResource(Res.string.folder_color_title),
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FolderColorPalette.options.forEach { option ->
                    val isSelected = option.key == colorKey
                    val swatchColor = option.color ?: MaterialTheme.colorScheme.primary
                    val colorLabel = stringResource(option.label)
                    IconButton(
                        onClick = { onColorSelected(option.key) },
                        modifier = Modifier.semantics {
                            contentDescription = colorLabel
                            selected = isSelected
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(swatchColor)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FolderInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(0.35f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.65f)
        )
    }
}
