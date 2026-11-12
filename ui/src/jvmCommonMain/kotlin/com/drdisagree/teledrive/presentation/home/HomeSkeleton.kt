package com.drdisagree.teledrive.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.drdisagree.teledrive.presentation.collection.CollectionType
import com.drdisagree.teledrive.presentation.components.skeleton.SkeletonBlock
import com.drdisagree.teledrive.presentation.components.skeleton.rememberShimmerBrush
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.common_loading
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeSkeleton(
    sections: HomeSections,
    showRecentSection: Boolean,
    showBackupCard: Boolean,
    showPendingSync: Boolean,
    showTrash: Boolean,
    showTransfers: Boolean,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val brush = rememberShimmerBrush()
    val loading = stringResource(Res.string.common_loading)
    val linkCount = CollectionType.entries.count { collection ->
        when (collection) {
            CollectionType.ARCHIVED -> sections.showArchived
            CollectionType.HIDDEN -> sections.showHidden
            else -> true
        }
    } + (if (showTrash) 1 else 0) + (if (showTransfers) 1 else 0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .clearAndSetSemantics { contentDescription = loading },
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        item(key = "drive-overview") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SkeletonBlock(brush, Modifier.fillMaxWidth(0.62f).height(32.dp))
                SkeletonBlock(brush, Modifier.fillMaxWidth(0.38f).height(20.dp))
            }
        }
        if (showBackupCard) {
            item {
                SkeletonBlock(
                    brush = brush,
                    modifier = Modifier.fillMaxWidth().height(BACKUP_CARD_HEIGHT),
                    shape = MaterialTheme.shapes.extraLarge
                )
            }
        }
        if (showPendingSync) {
            item(key = "pending-sync") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SkeletonBlock(brush, Modifier.size(24.dp), MaterialTheme.shapes.small)
                    Spacer(Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SkeletonBlock(brush, Modifier.fillMaxWidth(0.55f).height(14.dp))
                        SkeletonBlock(brush, Modifier.fillMaxWidth(0.72f).height(12.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    SkeletonBlock(brush, Modifier.width(76.dp).height(36.dp), MaterialTheme.shapes.large)
                }
            }
        }
        if (showRecentSection) {
            item {
                Column {
                    SectionHeaderSkeleton(brush)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        repeat(RECENT_TILES) { RecentTileSkeleton(brush) }
                    }
                }
            }
        }
        item {
            Column {
                SectionHeaderSkeleton(brush)
                Spacer(Modifier.height(8.dp))
                StorageCardSkeleton(brush)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(linkCount) { CollectionRowSkeleton(brush) }
            }
        }
    }
}

@Composable
private fun StorageCardSkeleton(brush: Brush) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(20.dp)
    ) {
        SkeletonBlock(brush, Modifier.width(128.dp).height(28.dp))
        Spacer(Modifier.height(6.dp))
        SkeletonBlock(brush, Modifier.width(92.dp).height(16.dp))
        Spacer(Modifier.height(16.dp))
        SkeletonBlock(brush, Modifier.fillMaxWidth().height(14.dp), MaterialTheme.shapes.extraLarge)
        Spacer(Modifier.height(16.dp))
        repeat(STORAGE_LEGEND_ROWS) { index ->
            if (index > 0) {
                Spacer(Modifier.height(5.dp))
                SkeletonBlock(
                    brush,
                    Modifier.fillMaxWidth().height(1.dp),
                    MaterialTheme.shapes.extraSmall
                )
                Spacer(Modifier.height(5.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                SkeletonBlock(brush, Modifier.size(12.dp), MaterialTheme.shapes.extraLarge)
                Spacer(Modifier.width(8.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SkeletonBlock(brush, Modifier.fillMaxWidth(0.56f).height(12.dp))
                    SkeletonBlock(brush, Modifier.fillMaxWidth(0.38f).height(12.dp))
                }
                Spacer(Modifier.width(12.dp))
                SkeletonBlock(brush, Modifier.width(32.dp).height(12.dp))
            }
        }
    }
}

@Composable
private fun SectionHeaderSkeleton(brush: Brush) {
    SkeletonBlock(brush, Modifier.width(96.dp).height(18.dp))
}

@Composable
private fun RecentTileSkeleton(brush: Brush) {
    Column(
        modifier = Modifier
            .width(112.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(6.dp)
    ) {
        SkeletonBlock(brush, Modifier.size(100.dp), MaterialTheme.shapes.medium)
        Spacer(Modifier.height(6.dp))
        SkeletonBlock(brush, Modifier.fillMaxWidth(0.8f).height(12.dp))
    }
}

@Composable
private fun CollectionRowSkeleton(brush: Brush) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkeletonBlock(brush, Modifier.size(24.dp), MaterialTheme.shapes.small)
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier.height(40.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
        ) {
            SkeletonBlock(brush, Modifier.fillMaxWidth(0.5f).height(14.dp))
            SkeletonBlock(brush, Modifier.fillMaxWidth(0.7f).height(10.dp))
        }
    }
}

private const val RECENT_TILES = 4
private val BACKUP_CARD_HEIGHT = 152.dp
private const val STORAGE_LEGEND_ROWS = 3
