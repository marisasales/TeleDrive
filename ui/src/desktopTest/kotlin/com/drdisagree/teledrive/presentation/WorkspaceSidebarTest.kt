package com.drdisagree.teledrive.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.redesign_identity
import com.drdisagree.teledrive.resources.transfers
import org.jetbrains.compose.resources.stringResource
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class WorkspaceSidebarTest {
    @get:Rule
    val rule = createComposeRule()

    private fun checkTransfers(collapsed: Boolean) {
        var transfersOpened = 0
        var transfersLabel = ""
        var oldIdentity = ""
        rule.setContent {
            MaterialTheme {
            transfersLabel = stringResource(Res.string.transfers)
                oldIdentity = stringResource(Res.string.redesign_identity)
                Box(Modifier.height(700.dp)) {
                    DesktopNavigationSidebar(
                        selectedDestination = null,
                        trashSelected = false,
                        transfersSelected = false,
                        collapsed = collapsed,
                        onToggleCollapsed = {},
                        onNavigate = {},
                        onOpenTrash = {},
                    onOpenTransfers = { transfersOpened++ },
                    activeTransferCount = 3,
                    trashItemCount = 2
                    )
                }
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText(oldIdentity).assertDoesNotExist()
        rule.onNodeWithText("3", useUnmergedTree = true).assertIsDisplayed()
        rule.onNodeWithText("2", useUnmergedTree = true).assertIsDisplayed()
        val transfers = if (collapsed) rule.onNodeWithContentDescription(transfersLabel)
        else rule.onNodeWithText(transfersLabel)
        transfers.assertIsDisplayed().performClick()
        rule.runOnIdle { assertEquals(1, transfersOpened) }
    }

    @Test
    fun expandedSidebarOpensTransfersWithoutIdentityText() = checkTransfers(collapsed = false)

    @Test
    fun collapsedSidebarKeepsLabeledTransfersAction() = checkTransfers(collapsed = true)
}
