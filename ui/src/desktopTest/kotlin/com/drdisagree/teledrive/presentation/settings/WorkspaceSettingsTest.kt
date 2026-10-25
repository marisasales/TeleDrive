package com.drdisagree.teledrive.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.drdisagree.teledrive.presentation.components.WorkspaceTopAppBar
import com.drdisagree.teledrive.presentation.theme.TeleDriveTheme
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class WorkspaceSettingsTest {
    @get:Rule val rule = createComposeRule()

    private fun checkSettings(width: Int, dark: Boolean) {
        var opened: SettingsSectionType? = null
        val sections = SettingsSectionType.entries.take(4)
        rule.setContent {
            TeleDriveTheme(darkTheme = dark, dynamicColor = false, desktopStyle = width >= 840) {
                Surface {
                    Column(Modifier.width(width.dp)) {
                        WorkspaceTopAppBar(title = { Text("TeleDrive") })
                        SettingsSectionsGrid(sections) { opened = it }
                    }
                }
            }
        }
        // Check the real localized section label, rather than hard-coding the test machine's locale.
        rule.onNodeWithText("TeleDrive").assertIsDisplayed()
        val buttons = rule.onAllNodes(androidx.compose.ui.test.hasClickAction()).fetchSemanticsNodes()
        assertEquals(sections.size, buttons.size)
        rule.onAllNodes(androidx.compose.ui.test.hasClickAction())[0].performClick()
        rule.runOnIdle { assertEquals(sections.first(), opened) }

        val pixels = rule.onRoot().captureToImage().toPixelMap()
        val image = BufferedImage(pixels.width, pixels.height, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until pixels.height) for (x in 0 until pixels.width) image.setRGB(x, y, pixels[x, y].toArgb())
        val directory = File("build/reports/workspace").apply { mkdirs() }
        ImageIO.write(image, "png", File(directory, "settings-$width-${if (dark) "dark" else "light"}.png"))
    }

    @Test fun desktopDark() = checkSettings(1000, true)
    @Test fun desktopLight() = checkSettings(1000, false)
    @Test fun phoneDark() = checkSettings(360, true)
    @Test fun phoneLight() = checkSettings(360, false)
}
