package com.drdisagree.teledrive.presentation.shortcuts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.drdisagree.teledrive.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

/** Reads the persisted overrides once per change, so every screen sees the same key map. */
@Composable
fun rememberShortcutBindings(): ShortcutBindings {
    val repository = koinInject<SettingsRepository>()
    val raw by repository.preferences.map { it.shortcuts }.collectAsState(initial = "")
    return remember(raw) { ShortcutBindings.parse(raw) }
}
