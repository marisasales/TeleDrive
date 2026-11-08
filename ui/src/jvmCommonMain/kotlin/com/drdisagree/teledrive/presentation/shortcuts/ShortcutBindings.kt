package com.drdisagree.teledrive.presentation.shortcuts

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.input.key.KeyEvent

/**
 * The active key map. Only the actions the user changed are stored; the rest fall back to
 * [ShortcutAction.default], so shipping new defaults reaches everyone who never edited them.
 */
class ShortcutBindings private constructor(
    private val overrides: Map<String, ShortcutBinding>
) {

    fun of(action: ShortcutAction): ShortcutBinding = overrides[action.id] ?: action.default

    fun matches(action: ShortcutAction, event: KeyEvent): Boolean = of(action).matches(event)

    fun isCustom(action: ShortcutAction): Boolean = overrides.containsKey(action.id)

    fun with(action: ShortcutAction, binding: ShortcutBinding): ShortcutBindings {
        val updated = overrides.toMutableMap()
        if (binding == action.default) updated.remove(action.id) else updated[action.id] = binding
        return ShortcutBindings(updated)
    }

    fun without(action: ShortcutAction): ShortcutBindings =
        ShortcutBindings(overrides - action.id)

    fun clear(): ShortcutBindings = Default

    /** Returns another action already using [binding], so the recorder can refuse duplicates. */
    fun conflict(action: ShortcutAction, binding: ShortcutBinding): ShortcutAction? =
        ShortcutAction.entries.firstOrNull { it != action && of(it) == binding }

    fun encode(): String = overrides.entries
        .sortedBy { it.key }
        .joinToString(";") { (id, binding) -> "$id=${binding.encode()}" }

    companion object {
        val Default = ShortcutBindings(emptyMap())

        fun parse(raw: String?): ShortcutBindings {
            if (raw.isNullOrBlank()) return Default
            val parsed = buildMap {
                raw.split(';').forEach { entry ->
                    val id = entry.substringBefore('=', "")
                    val value = entry.substringAfter('=', "")
                    if (id.isEmpty()) return@forEach
                    val action = ShortcutAction.entries.firstOrNull { it.id == id } ?: return@forEach
                    val binding = ShortcutBinding.decode(value) ?: return@forEach
                    put(action.id, binding)
                }
            }
            return ShortcutBindings(parsed)
        }
    }
}

val LocalShortcutBindings = staticCompositionLocalOf { ShortcutBindings.Default }
