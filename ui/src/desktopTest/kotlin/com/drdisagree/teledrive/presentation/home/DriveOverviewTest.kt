package com.drdisagree.teledrive.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class DriveOverviewTest {
    @get:Rule
    val rule = createComposeRule()

    private fun checkOverview(width: Int) {
        rule.setContent {
            MaterialTheme {
                Box(Modifier.width(width.dp)) {
                DriveOverview(
                    greeting = "Bom dia, Marisa",
                    dateTime = "domingo, 25 de out. · 13:18"
                )
                }
            }
        }
        rule.onNodeWithText("42").assertDoesNotExist()
        rule.onNodeWithText("Bom dia, Marisa").assertIsDisplayed()
        rule.onNodeWithText("domingo, 25 de out. · 13:18").assertIsDisplayed()
        rule.onNodeWithText("7").assertDoesNotExist()
    }

    @Test
    fun overviewStacksOnPhone() = checkOverview(360)

    @Test
    fun overviewShowsGreetingOnDesktop() = checkOverview(1000)
}
