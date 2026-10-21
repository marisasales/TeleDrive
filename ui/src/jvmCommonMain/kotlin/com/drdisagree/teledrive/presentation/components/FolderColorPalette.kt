package com.drdisagree.teledrive.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.drdisagree.teledrive.presentation.theme.ChartPalette
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.folder_color_amber
import com.drdisagree.teledrive.resources.folder_color_blue
import com.drdisagree.teledrive.resources.folder_color_default
import com.drdisagree.teledrive.resources.folder_color_rose
import com.drdisagree.teledrive.resources.folder_color_slate
import com.drdisagree.teledrive.resources.folder_color_teal
import com.drdisagree.teledrive.resources.folder_color_title
import com.drdisagree.teledrive.resources.folder_color_violet
import org.jetbrains.compose.resources.StringResource

internal data class FolderColorOption(
    val key: String?,
    val color: Color?,
    val label: StringResource
)

internal object FolderColorPalette {
    val options = listOf(
        FolderColorOption(null, null, Res.string.folder_color_default),
        FolderColorOption("blue", ChartPalette.Blue, Res.string.folder_color_blue),
        FolderColorOption("violet", ChartPalette.Violet, Res.string.folder_color_violet),
        FolderColorOption("teal", ChartPalette.Teal, Res.string.folder_color_teal),
        FolderColorOption("amber", ChartPalette.Amber, Res.string.folder_color_amber),
        FolderColorOption("rose", ChartPalette.Rose, Res.string.folder_color_rose),
        FolderColorOption("slate", ChartPalette.Slate, Res.string.folder_color_slate)
    )

    fun colorForKey(key: String?): Color? = options.firstOrNull { it.key == key }?.color
}

@Composable
internal fun FolderColorMenu(
    selectedColorKey: String?,
    onColorSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val menuLabel = org.jetbrains.compose.resources.stringResource(Res.string.folder_color_title)

    Box(modifier) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.semantics { contentDescription = menuLabel }
        ) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Text(
                text = menuLabel,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            FolderColorPalette.options.forEach { option ->
                val colorLabel = org.jetbrains.compose.resources.stringResource(option.label)
                val swatchColor = option.color ?: MaterialTheme.colorScheme.primary
                DropdownMenuItem(
                    text = { Text(colorLabel) },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(swatchColor, CircleShape)
                                .border(
                                    width = if (option.key == selectedColorKey) 2.dp else 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                        )
                    },
                    trailingIcon = {
                        if (option.key == selectedColorKey) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                        }
                    },
                    onClick = {
                        onColorSelected(option.key)
                        expanded = false
                    }
                )
            }
        }
    }
}
