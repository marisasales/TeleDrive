package com.drdisagree.teledrive.presentation.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TeleDriveTheme(
    darkTheme: Boolean = systemDarkTheme(),
    dynamicColor: Boolean = true,
    desktopStyle: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseColorScheme = platformColorScheme(darkTheme, dynamicColor)
        ?: if (darkTheme) BlueDarkColorScheme else BlueLightColorScheme
    val colorScheme = if (desktopStyle) {
        if (darkTheme) baseColorScheme.copy(
            background = Color(0xFF11151B),
            surface = Color(0xFF11151B),
            surfaceContainerLowest = Color(0xFF151A21),
            surfaceContainerLow = Color(0xFF191F27),
            surfaceContainer = Color(0xFF1D242D),
            surfaceContainerHigh = Color(0xFF252D37),
            surfaceContainerHighest = Color(0xFF2C3540)
        ) else baseColorScheme.copy(
            background = Color(0xFFF5F7FA),
            surface = Color(0xFFF5F7FA),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF9FAFC),
            surfaceContainer = Color(0xFFF0F3F7),
            surfaceContainerHigh = Color(0xFFE9EEF4),
            surfaceContainerHighest = Color(0xFFE2E8F0)
        )
    } else baseColorScheme

    PlatformThemeSideEffects(darkTheme)

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        typography = TeleDriveTypography,
        shapes = if (desktopStyle) DesktopTeleDriveShapes else TeleDriveShapes,
        content = content
    )
}
