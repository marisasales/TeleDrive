package com.drdisagree.teledrive.presentation.shortcuts

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key

/**
 * A key chord. Stored as the raw [Key.keyCode] so it survives the round trip through preferences
 * without depending on enum ordinals.
 */
data class ShortcutBinding(
    val keyCode: Long,
    val ctrl: Boolean = false,
    val alt: Boolean = false,
    val shift: Boolean = false,
    val meta: Boolean = false
) {

    val key: Key get() = Key(keyCode)

    fun matches(event: KeyEvent): Boolean =
        keyMatches(event) &&
                event.isCtrlPressed == ctrl &&
                event.isAltPressed == alt &&
                event.isShiftPressed == shift &&
                event.isMetaPressed == meta

    /** Numpad Enter behaves like Enter, as it did before shortcuts became rebindable. */
    private fun keyMatches(event: KeyEvent): Boolean =
        event.key.keyCode == keyCode ||
                (keyCode == Key.Enter.keyCode && event.key == Key.NumPadEnter)

    /** "Ctrl+Shift+N" on desktop, the same wording everywhere for consistency. */
    fun display(): String {
        val parts = buildList {
            if (ctrl) add("Ctrl")
            if (alt) add("Alt")
            if (shift) add("Shift")
            if (meta) add("Meta")
            add(keyLabel(keyCode))
        }
        return parts.joinToString("+")
    }

    fun encode(): String = buildString {
        append(if (ctrl) 1 else 0)
        append(if (alt) 1 else 0)
        append(if (shift) 1 else 0)
        append(if (meta) 1 else 0)
        append(':')
        append(keyCode)
    }

    companion object {
        /** Parses the tail produced by [encode]. */
        fun decode(value: String): ShortcutBinding? {
            val flags = value.substringBefore(':', "")
            val code = value.substringAfter(':', "").toLongOrNull() ?: return null
            if (flags.length != 4) return null
            return ShortcutBinding(
                keyCode = code,
                ctrl = flags[0] == '1',
                alt = flags[1] == '1',
                shift = flags[2] == '1',
                meta = flags[3] == '1'
            )
        }

        fun keyLabel(keyCode: Long): String = KEY_LABELS[keyCode] ?: "Key $keyCode"

        private val KEY_LABELS: Map<Long, String> = buildMap {
            val letters = listOf(
                Key.A, Key.B, Key.C, Key.D, Key.E, Key.F, Key.G, Key.H, Key.I, Key.J, Key.K, Key.L,
                Key.M, Key.N, Key.O, Key.P, Key.Q, Key.R, Key.S, Key.T, Key.U, Key.V, Key.W, Key.X,
                Key.Y, Key.Z
            )
            letters.forEachIndexed { index, key -> put(key.keyCode, ('A' + index).toString()) }

            val digits = listOf(
                Key.Zero, Key.One, Key.Two, Key.Three, Key.Four,
                Key.Five, Key.Six, Key.Seven, Key.Eight, Key.Nine
            )
            digits.forEachIndexed { index, key -> put(key.keyCode, index.toString()) }

            val functions = listOf(
                Key.F1, Key.F2, Key.F3, Key.F4, Key.F5, Key.F6,
                Key.F7, Key.F8, Key.F9, Key.F10, Key.F11, Key.F12
            )
            functions.forEachIndexed { index, key -> put(key.keyCode, "F${index + 1}") }

            put(Key.DirectionUp.keyCode, "↑")
            put(Key.DirectionDown.keyCode, "↓")
            put(Key.DirectionLeft.keyCode, "←")
            put(Key.DirectionRight.keyCode, "→")
            put(Key.Enter.keyCode, "Enter")
            put(Key.NumPadEnter.keyCode, "Enter")
            put(Key.Escape.keyCode, "Esc")
            put(Key.Tab.keyCode, "Tab")
            put(Key.Spacebar.keyCode, "Space")
            put(Key.Backspace.keyCode, "Backspace")
            put(Key.Delete.keyCode, "Delete")
            put(Key.MoveHome.keyCode, "Home")
            put(Key.MoveEnd.keyCode, "End")
            put(Key.PageUp.keyCode, "Page Up")
            put(Key.PageDown.keyCode, "Page Down")
            put(Key.Minus.keyCode, "-")
            put(Key.Equals.keyCode, "=")
            put(Key.Comma.keyCode, ",")
            put(Key.Period.keyCode, ".")
            put(Key.Slash.keyCode, "/")
            put(Key.Backslash.keyCode, "\\")
            put(Key.Semicolon.keyCode, ";")
            put(Key.Apostrophe.keyCode, "'")
            put(Key.LeftBracket.keyCode, "[")
            put(Key.RightBracket.keyCode, "]")
            put(Key.Grave.keyCode, "`")
            put(Key.Insert.keyCode, "Insert")
        }
    }
}
