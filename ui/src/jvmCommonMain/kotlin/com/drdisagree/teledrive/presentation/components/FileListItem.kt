package com.drdisagree.teledrive.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drdisagree.teledrive.domain.model.DriveFile
import com.drdisagree.teledrive.presentation.common.Formatters
import com.drdisagree.teledrive.presentation.platform.LocalPlatformCapabilities
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.common_content_description_available_offline
import com.drdisagree.teledrive.resources.common_content_description_favorite
import com.drdisagree.teledrive.resources.common_content_description_selected
import com.drdisagree.teledrive.resources.common_file_size_and_date
import com.drdisagree.teledrive.resources.common_open
import com.drdisagree.teledrive.resources.common_select
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListItem(
    file: DriveFile,
    selected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    clickToSelect: Boolean = false,
    onDoubleClick: () -> Unit = onClick,
    modifier: Modifier = Modifier,
    showFavorite: Boolean = true,
    showAvailableOffline: Boolean = true,
    desktopColumns: Boolean = false,
    status: (@Composable () -> Unit)? = null
) {
    val compact = LocalCompactLayout.current
    val desktop = LocalPlatformCapabilities.current.isDesktop
    val selectionLabel = "${stringResource(Res.string.common_select)}: ${file.name}"
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val background by animateColorAsState(
        targetValue = when {
            selected -> MaterialTheme.colorScheme.secondaryContainer
            desktop && hovered -> MaterialTheme.colorScheme.surfaceContainerHigh
            else -> Color.Transparent
        },
        label = "fileRowHover"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(background)
            .then(if (desktop) Modifier.hoverable(interactionSource) else Modifier)
            .combinedClickable(
                onClickLabel = stringResource(
                    if (selectionMode || (desktop && clickToSelect)) {
                        Res.string.common_select
                    } else {
                        Res.string.common_open
                    }
                ),
                onLongClickLabel = stringResource(Res.string.common_select),
                onClick = if (desktop && clickToSelect) onLongClick else onClick,
                onDoubleClick = if (desktop && clickToSelect) onDoubleClick else null,
                onLongClick = onLongClick
            )
            .padding(horizontal = 12.dp, vertical = if (compact) 4.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (desktop && (!clickToSelect || selected)) {
            Checkbox(
                checked = selected,
                onCheckedChange = { onLongClick() },
                modifier = Modifier.semantics { contentDescription = selectionLabel }
            )
        }
        Box {
            FileThumbnail(
                file = file,
                contentDescription = null,
                modifier = Modifier
                    .size(if (compact) 38.dp else 48.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            if (!desktop && selected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(Res.string.common_content_description_selected),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(Modifier.width(if (compact) 10.dp else 14.dp))
        if (desktop && desktopColumns) {
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(2.2f)
            )
            Text(
                text = file.extension.ifBlank { file.mimeType.substringAfter('/', "—") }
                    .uppercase(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.8f)
            )
            Text(
                text = Formatters.bytes(file.sizeBytes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.width(88.dp)
            )
            Text(
                text = Formatters.date(file.modifiedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.width(112.dp)
            )
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
                if (!compact) {
                    Text(
                        text = stringResource(
                            Res.string.common_file_size_and_date,
                            Formatters.bytes(file.sizeBytes),
                            Formatters.date(file.modifiedAt)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        if (file.isFavorite && showFavorite) {
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = stringResource(Res.string.common_content_description_favorite),
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
        if (file.isAvailableOffline && showAvailableOffline) {
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Filled.OfflinePin,
                contentDescription = stringResource(
                    Res.string.common_content_description_available_offline
                ),
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        if (status != null) {
            Spacer(Modifier.width(8.dp))
            status()
        }
        Spacer(Modifier.width(8.dp))
        BackupStateBadge(file = file)
    }
}
