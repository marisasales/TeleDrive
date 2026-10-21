package com.drdisagree.teledrive.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.drdisagree.teledrive.domain.model.DriveFolder
import com.drdisagree.teledrive.domain.model.DriveFile
import com.drdisagree.teledrive.presentation.platform.LocalPlatformCapabilities
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.common_content_description_available_offline
import com.drdisagree.teledrive.resources.common_content_description_favorite
import com.drdisagree.teledrive.resources.common_select
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderGridItem(
    folder: DriveFolder,
    selected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    previewFiles: List<DriveFile> = emptyList(),
    modifier: Modifier = Modifier
) {
    val isDesktop = LocalPlatformCapabilities.current.isDesktop
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
            .combinedClickable(
                onLongClickLabel = stringResource(Res.string.common_select),
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        if (isDesktop) {
            DesktopFolderPreview(files = previewFiles)
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Folder,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
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

@Composable
private fun DesktopFolderPreview(files: List<DriveFile>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .aspectRatio(1.55f),
        contentAlignment = Alignment.Center
    ) {
        if (files.isEmpty()) {
            Icon(
                imageVector = Icons.Filled.Folder,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                files.chunked(2).forEach { rowFiles ->
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        rowFiles.forEach { file ->
                            FileThumbnail(
                                file = file,
                                modifier = Modifier.weight(1f).fillMaxSize().clip(RoundedCornerShape(7.dp)),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                allowThumbnailGeneration = false
                            )
                        }
                        if (rowFiles.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(2.dp))
}
