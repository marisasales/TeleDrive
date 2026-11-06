package com.drdisagree.teledrive.presentation.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.PhotoLibrary
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.drdisagree.teledrive.core.media.ThumbnailModel
import com.drdisagree.teledrive.domain.model.MediaAlbum
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.album_not_in_folder
import com.drdisagree.teledrive.resources.common_actions
import com.drdisagree.teledrive.resources.files_hide
import com.drdisagree.teledrive.resources.item_count
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlbumCard(
    album: MediaAlbum,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    /** Available only for albums backed by a real folder; unfiled albums cannot be hidden. */
    onHide: (() -> Unit)? = null
) {
    val albumName = album.name ?: stringResource(Res.string.album_not_in_folder)
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f), MaterialTheme.shapes.large)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.3f)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            val coverFileId = album.coverFileId
            if (coverFileId != null) {
                AsyncImage(
                    model = ThumbnailModel(coverFileId),
                    contentDescription = albumName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (onHide != null) {
                var menuOpen by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    IconButton(
                        onClick = { menuOpen = true },
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.Black.copy(alpha = 0.38f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(Res.string.common_actions),
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.files_hide)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.VisibilityOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            onClick = {
                                menuOpen = false
                                onHide()
                            }
                        )
                    }
                }
            }
        }
        Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 12.dp)) {
            Text(
                text = albumName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = pluralStringResource(Res.plurals.item_count, album.itemCount, album.itemCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
