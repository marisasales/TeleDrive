package com.drdisagree.teledrive.presentation.shortcuts

import androidx.compose.ui.input.key.Key
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.shortcut_action_back
import com.drdisagree.teledrive.resources.shortcut_action_clear_selection
import com.drdisagree.teledrive.resources.shortcut_action_close_preview
import com.drdisagree.teledrive.resources.shortcut_action_cut
import com.drdisagree.teledrive.resources.shortcut_action_delete
import com.drdisagree.teledrive.resources.shortcut_action_new_folder
import com.drdisagree.teledrive.resources.shortcut_action_next_preview
import com.drdisagree.teledrive.resources.shortcut_action_open
import com.drdisagree.teledrive.resources.shortcut_action_paste
import com.drdisagree.teledrive.resources.shortcut_action_previous_preview
import com.drdisagree.teledrive.resources.shortcut_action_rename
import com.drdisagree.teledrive.resources.shortcut_action_search
import com.drdisagree.teledrive.resources.shortcut_action_select_all
import com.drdisagree.teledrive.resources.shortcut_group_items
import com.drdisagree.teledrive.resources.shortcut_group_navigation
import com.drdisagree.teledrive.resources.shortcut_group_preview
import com.drdisagree.teledrive.resources.shortcut_group_selection
import org.jetbrains.compose.resources.StringResource

enum class ShortcutGroup(val labelRes: StringResource) {
    NAVIGATION(Res.string.shortcut_group_navigation),
    ITEMS(Res.string.shortcut_group_items),
    SELECTION(Res.string.shortcut_group_selection),
    PREVIEW(Res.string.shortcut_group_preview)
}

/**
 * Every rebindable command. Only commands handled by our own code live here; text-field editing
 * (Ctrl+C/V inside a field) and mouse gestures stay fixed.
 */
enum class ShortcutAction(
    val id: String,
    val labelRes: StringResource,
    val group: ShortcutGroup,
    val default: ShortcutBinding
) {
    SEARCH(
        id = "search",
        labelRes = Res.string.shortcut_action_search,
        group = ShortcutGroup.NAVIGATION,
        default = ShortcutBinding(Key.F.keyCode, ctrl = true)
    ),
    NEW_FOLDER(
        id = "new_folder",
        labelRes = Res.string.shortcut_action_new_folder,
        group = ShortcutGroup.NAVIGATION,
        default = ShortcutBinding(Key.N.keyCode, ctrl = true, shift = true)
    ),
    OPEN(
        id = "open",
        labelRes = Res.string.shortcut_action_open,
        group = ShortcutGroup.ITEMS,
        default = ShortcutBinding(Key.Enter.keyCode)
    ),
    BACK(
        id = "back",
        labelRes = Res.string.shortcut_action_back,
        group = ShortcutGroup.NAVIGATION,
        default = ShortcutBinding(Key.Backspace.keyCode)
    ),
    RENAME(
        id = "rename",
        labelRes = Res.string.shortcut_action_rename,
        group = ShortcutGroup.ITEMS,
        default = ShortcutBinding(Key.F2.keyCode)
    ),
    DELETE(
        id = "delete",
        labelRes = Res.string.shortcut_action_delete,
        group = ShortcutGroup.ITEMS,
        default = ShortcutBinding(Key.Delete.keyCode)
    ),
    SELECT_ALL(
        id = "select_all",
        labelRes = Res.string.shortcut_action_select_all,
        group = ShortcutGroup.SELECTION,
        default = ShortcutBinding(Key.A.keyCode, ctrl = true)
    ),
    CUT(
        id = "cut",
        labelRes = Res.string.shortcut_action_cut,
        group = ShortcutGroup.SELECTION,
        default = ShortcutBinding(Key.X.keyCode, ctrl = true)
    ),
    PASTE(
        id = "paste",
        labelRes = Res.string.shortcut_action_paste,
        group = ShortcutGroup.SELECTION,
        default = ShortcutBinding(Key.V.keyCode, ctrl = true)
    ),
    CLEAR_SELECTION(
        id = "clear_selection",
        labelRes = Res.string.shortcut_action_clear_selection,
        group = ShortcutGroup.SELECTION,
        default = ShortcutBinding(Key.Escape.keyCode)
    ),
    PREVIOUS_PREVIEW(
        id = "previous_preview",
        labelRes = Res.string.shortcut_action_previous_preview,
        group = ShortcutGroup.PREVIEW,
        default = ShortcutBinding(Key.DirectionLeft.keyCode)
    ),
    NEXT_PREVIEW(
        id = "next_preview",
        labelRes = Res.string.shortcut_action_next_preview,
        group = ShortcutGroup.PREVIEW,
        default = ShortcutBinding(Key.DirectionRight.keyCode)
    ),
    CLOSE_PREVIEW(
        id = "close_preview",
        labelRes = Res.string.shortcut_action_close_preview,
        group = ShortcutGroup.PREVIEW,
        default = ShortcutBinding(Key.Escape.keyCode)
    )
}
