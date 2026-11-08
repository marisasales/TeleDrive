package com.drdisagree.teledrive.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.LaunchedEffect
import com.drdisagree.teledrive.presentation.shortcuts.ShortcutAction
import com.drdisagree.teledrive.presentation.shortcuts.ShortcutBinding
import com.drdisagree.teledrive.presentation.shortcuts.ShortcutBindings
import com.drdisagree.teledrive.presentation.shortcuts.ShortcutGroup
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.shortcut_action_close_preview
import com.drdisagree.teledrive.resources.shortcuts_cancel
import com.drdisagree.teledrive.resources.shortcuts_conflict
import com.drdisagree.teledrive.resources.shortcuts_conflict_title
import com.drdisagree.teledrive.resources.shortcuts_edit
import com.drdisagree.teledrive.resources.shortcuts_intro
import com.drdisagree.teledrive.resources.shortcuts_press_keys
import com.drdisagree.teledrive.resources.shortcuts_reset
import com.drdisagree.teledrive.resources.shortcuts_reset_all
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ShortcutsSection(viewModel: SettingsViewModel) {
    val bindings = com.drdisagree.teledrive.presentation.shortcuts.rememberShortcutBindings()
    var capturing by remember { mutableStateOf<ShortcutAction?>(null) }
    var conflict by remember { mutableStateOf<ShortcutAction?>(null) }

    Column(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            stringResource(Res.string.shortcuts_intro),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )
        ShortcutGroup.entries.forEach { group ->
            SettingsSectionTitle(stringResource(group.labelRes))
            SettingsGroup {
                ShortcutAction.entries.filter { it.group == group }.forEach { action ->
                    add {
                        ShortcutRow(
                            action = action,
                            binding = bindings.of(action),
                            custom = bindings.isCustom(action),
                            onEdit = { capturing = action; conflict = null },
                            onReset = {
                                updateBindings(viewModel) { it.without(action) }
                            }
                        )
                    }
                }
            }
        }
        TextButton(
            onClick = { updateBindings(viewModel) { it.clear() } },
            modifier = Modifier.padding(start = 8.dp, top = 12.dp)
        ) { Text(stringResource(Res.string.shortcuts_reset_all)) }
    }

    capturing?.let { action ->
        KeyCaptureDialog(
            action = action,
            conflict = conflict,
            bindings = bindings,
            onCaptured = { binding ->
                updateBindings(viewModel) { it.with(action, binding) }
                capturing = null
                conflict = null
            },
            onConflict = { conflict = it },
            onDismiss = { capturing = null; conflict = null }
        )
    }
}

@Composable
private fun ShortcutRow(
    action: ShortcutAction,
    binding: ShortcutBinding,
    custom: Boolean,
    onEdit: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onEdit).padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(stringResource(action.labelRes), style = MaterialTheme.typography.bodyLarge)
            if (custom) Text(
                stringResource(Res.string.shortcuts_reset),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable(onClick = onReset).padding(top = 4.dp)
            )
        }
        Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surfaceVariant) {
            Text(binding.display(), modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge)
        }
        TextButton(onClick = onEdit) { Text(stringResource(Res.string.shortcuts_edit)) }
    }
}

@Composable
private fun KeyCaptureDialog(
    action: ShortcutAction,
    conflict: ShortcutAction?,
    bindings: ShortcutBindings,
    onCaptured: (ShortcutBinding) -> Unit,
    onConflict: (ShortcutAction) -> Unit,
    onDismiss: () -> Unit
) {
    val requester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) { requester.requestFocus() }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.widthIn(max = 440.dp).fillMaxWidth().padding(24.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(stringResource(action.labelRes), style = MaterialTheme.typography.headlineSmall)
                Text(
                    if (conflict == null) stringResource(Res.string.shortcuts_press_keys)
                    else stringResource(Res.string.shortcuts_conflict, stringResource(conflict.labelRes)),
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 20.dp)
                        .focusRequester(requester)
                        .onPreviewKeyEvent { event ->
                            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                            if (event.key == Key.Escape) { onDismiss(); return@onPreviewKeyEvent true }
                            val ignored = setOf(Key.CtrlLeft, Key.CtrlRight, Key.ShiftLeft, Key.ShiftRight,
                                Key.AltLeft, Key.AltRight, Key.MetaLeft, Key.MetaRight)
                            if (event.key in ignored) return@onPreviewKeyEvent true
                            val binding = ShortcutBinding(
                                event.key.keyCode,
                                ctrl = event.isCtrlPressed,
                                alt = event.isAltPressed,
                                shift = event.isShiftPressed,
                                meta = event.isMetaPressed
                            )
                            val duplicate = bindings.conflict(action, binding)
                            if (duplicate != null) onConflict(duplicate) else onCaptured(binding)
                            true
                        }
                        .focusable(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text(stringResource(Res.string.shortcuts_cancel)) }
                }
            }
        }
    }
}

private fun updateBindings(viewModel: SettingsViewModel, transform: (ShortcutBindings) -> ShortcutBindings) {
    viewModel.update { preferences ->
        preferences.copy(shortcuts = transform(ShortcutBindings.parse(preferences.shortcuts)).encode())
    }
}
