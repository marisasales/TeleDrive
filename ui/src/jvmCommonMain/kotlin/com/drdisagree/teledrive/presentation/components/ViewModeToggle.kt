package com.drdisagree.teledrive.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.drdisagree.teledrive.domain.model.ViewMode

@Composable
fun ViewModeToggle(
    viewMode: ViewMode,
    onModeChange: (ViewMode) -> Unit,
    gridDescription: String,
    listDescription: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val outline = MaterialTheme.colorScheme.outlineVariant
    Row(
        modifier
            .clip(shape)
            .border(1.dp, outline, shape)
            .selectableGroup()
    ) {
        ViewModeOption(
            selected = viewMode == ViewMode.GRID,
            description = gridDescription,
            onClick = { onModeChange(ViewMode.GRID) },
            glyph = ViewModeGlyph.Grid
        )
        ViewModeOption(
            selected = viewMode == ViewMode.LIST,
            description = listDescription,
            onClick = { onModeChange(ViewMode.LIST) },
            glyph = ViewModeGlyph.List
        )
    }
}

@Composable
private fun ViewModeOption(
    selected: Boolean,
    description: String,
    onClick: () -> Unit,
    glyph: ViewModeGlyph
) {
    val shape = RoundedCornerShape(9.dp)
    val tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(
        Modifier
            .size(48.dp)
            .clip(shape)
            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .semantics { contentDescription = description }
            .padding(16.dp)
            .fillMaxSize()
    ) {
        val stroke = 1.7.dp.toPx()
        val gap = 2.dp.toPx()
        if (glyph == ViewModeGlyph.Grid) {
            val cell = (size.minDimension - gap * 2) / 3
            for (row in 0..2) {
                for (column in 0..2) {
                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(column * (cell + gap), row * (cell + gap)),
                        size = androidx.compose.ui.geometry.Size(cell, cell),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(stroke)
                    )
                }
            }
        } else {
            val dot = size.minDimension * 0.13f
            val lineStart = dot * 2.2f
            val lineEnd = size.width
            for (row in 0..2) {
                val y = dot + row * (size.height - dot * 2) / 2
                drawCircle(tint, radius = dot / 2, center = Offset(dot / 2, y))
                drawLine(
                    color = tint,
                    start = Offset(lineStart, y),
                    end = Offset(lineEnd, y),
                    strokeWidth = stroke
                )
            }
        }
    }
}

private enum class ViewModeGlyph { Grid, List }
