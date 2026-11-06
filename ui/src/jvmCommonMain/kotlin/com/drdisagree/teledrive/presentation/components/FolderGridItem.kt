package com.drdisagree.teledrive.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drdisagree.teledrive.domain.model.DriveFolder
import com.drdisagree.teledrive.presentation.platform.LocalPlatformCapabilities
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.common_content_description_available_offline
import com.drdisagree.teledrive.resources.common_content_description_favorite
import com.drdisagree.teledrive.resources.common_open
import com.drdisagree.teledrive.resources.common_select
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderGridItem(
    folder: DriveFolder,
    selected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    clickToSelect: Boolean = false,
    onDoubleClick: () -> Unit = onClick,
    folderColor: Color? = null,
    folderColorKey: String? = null,
    onFolderColorSelected: (String?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDesktop = LocalPlatformCapabilities.current.isDesktop
    val selectionLabel = stringResource(Res.string.common_select) + ": " + folder.name
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .then(
                if (isDesktop) Modifier.border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                    MaterialTheme.shapes.large
                ) else Modifier
            )
            .background(
                if (selected) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.surfaceContainerLow
            )
            .semantics { if (selected) this.selected = true }
            .combinedClickable(
                onClickLabel = stringResource(
                    if (isDesktop && clickToSelect) Res.string.common_select else Res.string.common_open
                ),
                onLongClickLabel = stringResource(Res.string.common_select),
                onClick = if (isDesktop && clickToSelect) onLongClick else onClick,
                onDoubleClick = if (isDesktop && clickToSelect) onDoubleClick else null,
                onLongClick = onLongClick
            )
    ) {
        if (isDesktop) {
            // Compact vertical card: controls float over the corners so the icon, name and
            // count keep the whole height instead of leaving a large empty band.
            Box(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, top = 14.dp, bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier.height(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(42.dp),
                            tint = folderColor ?: MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = folder.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = folder.contentsLabel(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
                if (!clickToSelect || selected) {
                    Checkbox(
                        checked = selected,
                        onCheckedChange = { onLongClick() },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .semantics {
                                contentDescription = selectionLabel
                            }
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(start = 4.dp, end = 2.dp, top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (folder.isFavorite) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = stringResource(Res.string.common_content_description_favorite),
                            modifier = Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    if (folder.isAvailableOffline) {
                        Icon(
                            imageVector = Icons.Filled.OfflinePin,
                            contentDescription = stringResource(
                                Res.string.common_content_description_available_offline
                            ),
                            modifier = Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.width(2.dp))
                    }
                    FolderColorMenu(
                        selectedColorKey = folderColorKey,
                        onColorSelected = onFolderColorSelected
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Folder,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = folderColor ?: MaterialTheme.colorScheme.primary
                )
            }
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = folder.name,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (folder.isFavorite) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = stringResource(Res.string.common_content_description_favorite),
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    if (folder.isAvailableOffline) {
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
                }
                Text(
                    text = folder.contentsLabel(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
