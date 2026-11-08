package com.drdisagree.teledrive.presentation.components

import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import com.drdisagree.teledrive.presentation.shortcuts.LocalShortcutBindings
import com.drdisagree.teledrive.presentation.shortcuts.ShortcutAction

@Composable
fun Modifier.backspaceUp(onUp: (() -> Unit)?): Modifier {
    val bindings = LocalShortcutBindings.current
    return onPreviewKeyEvent { event ->
    val up = bindings.matches(ShortcutAction.BACK, event) ||
            event.isAltPressed && event.key == Key.DirectionLeft
    if (onUp == null || !up || event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
    KeyboardHandoff.armed = true
    onUp()
    true
    }
}
