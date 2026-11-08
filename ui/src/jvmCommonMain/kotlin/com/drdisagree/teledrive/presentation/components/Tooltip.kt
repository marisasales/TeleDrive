package com.drdisagree.teledrive.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.drdisagree.teledrive.domain.model.UserPreferences
import com.drdisagree.teledrive.domain.repository.SettingsRepository
import org.koin.compose.koinInject

@Composable
fun TipIconButton(
    tooltip: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color? = null
) {
    TooltippedContent(tooltip, modifier) {
        IconButton(onClick = onClick, enabled = enabled) {
            Icon(icon, contentDescription = tooltip, tint = tint ?: MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltippedContent(
    tooltip: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val settingsRepository = koinInject<SettingsRepository>()
    val preferences by settingsRepository.preferences.collectAsState(initial = UserPreferences())
    if (!preferences.showTooltips) {
        Box(modifier = modifier) { content() }
        return
    }
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(tooltip) } },
        state = rememberTooltipState(),
        modifier = modifier,
        content = content
    )
}
