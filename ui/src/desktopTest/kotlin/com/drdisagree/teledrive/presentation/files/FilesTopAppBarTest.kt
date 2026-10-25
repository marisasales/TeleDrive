package com.drdisagree.teledrive.presentation.files

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.dp
import com.drdisagree.teledrive.domain.model.ViewMode
import com.drdisagree.teledrive.presentation.components.ViewModeToggle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
class FilesTopAppBarTest {
    @get:Rule
    val rule = createComposeRule()

    private fun checkLayout(width: Int, inlineSearch: Boolean) {
        var searchClicks = 0
        rule.setContent {
            MaterialTheme {
                Box(Modifier.width(width.dp).testTag("toolbar")) {
                    FilesTopAppBar(
                        title = "Arquivos",
                        colors = TopAppBarDefaults.topAppBarColors(),
                        navigationIcon = {
                            IconButton(onClick = {}, modifier = Modifier.testTag("back")) { Text("←") }
                        }
                    ) { showInlineSearch, searchModifier ->
                        IconButton(onClick = {}, modifier = Modifier.testTag("refresh")) { Text("R") }
                        if (showInlineSearch) {
                            ShortcutTooltip("Ctrl+F", modifier = searchModifier) {
                                FilesSearchField(
                                    modifier = Modifier.fillMaxWidth().testTag("search"),
                                    onClick = { searchClicks++ }
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { searchClicks++ },
                                modifier = Modifier.testTag("compact-search")
                            ) { Text("S") }
                        }
                        IconButton(onClick = {}, modifier = Modifier.testTag("new-folder")) { Text("N") }
                        ViewModeToggle(
                            viewMode = ViewMode.GRID,
                            onModeChange = {},
                            gridDescription = "Grid",
                            listDescription = "List",
                            modifier = Modifier.testTag("view-mode")
                        )
                        IconButton(onClick = {}, modifier = Modifier.testTag("sort")) { Text("O") }
                    }
                }
            }
        }
        val toolbar = rule.onNodeWithTag("toolbar").fetchSemanticsNode().boundsInRoot
        rule.onNodeWithTag("view-mode").assertHeightIsEqualTo(48.dp)
        for (tag in listOf("refresh", "new-folder", "view-mode", "sort")) {
            val node = rule.onNodeWithTag(tag).assertIsDisplayed().fetchSemanticsNode().boundsInRoot
            assertTrue("$tag must stay inside the toolbar", node.left >= toolbar.left && node.right <= toolbar.right)
        }
        rule.onNodeWithText("Arquivos").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { get ->
            val results = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
            get(results)
            assertEquals(1, results.single().lineCount)
        }
        val searchTag = if (inlineSearch) "search" else "compact-search"
        rule.onNodeWithTag(if (inlineSearch) "compact-search" else "search").assertDoesNotExist()
        rule.onNodeWithTag(searchTag).assertIsDisplayed().performClick()
        rule.runOnIdle { assertEquals(1, searchClicks) }
        if (inlineSearch) {
            val search = rule.onNodeWithTag("search").fetchSemanticsNode().boundsInRoot
            val folder = rule.onNodeWithTag("new-folder").fetchSemanticsNode().boundsInRoot
            val title = rule.onNodeWithText("Arquivos").fetchSemanticsNode().boundsInRoot
            assertTrue("Search must not overlap title or actions", search.left >= title.right && search.right <= folder.left)
            assertTrue("Search should sit near the toolbar center", abs(search.center.x - toolbar.center.x) <= 24f)
        }
    }

    @Test
    fun `desktop search leaves room for title and actions`() = checkLayout(1000, inlineSearch = true)

    @Test
    fun `wide desktop centers the search field`() = checkLayout(1440, inlineSearch = true)

    @Test
    fun `medium window keeps all toolbar controls visible`() = checkLayout(600, inlineSearch = true)

    @Test
    fun `phone uses compact search without wrapping title`() = checkLayout(375, inlineSearch = false)
}
