package com.drdisagree.teledrive.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drdisagree.teledrive.core.telegram.TelegramConnectionState
import com.drdisagree.teledrive.domain.model.BackupHold
import com.drdisagree.teledrive.domain.model.BackupSessionStatus
import com.drdisagree.teledrive.domain.model.FileSortField
import com.drdisagree.teledrive.domain.model.SortDirection
import com.drdisagree.teledrive.domain.model.StorageSlice
import com.drdisagree.teledrive.presentation.collection.CollectionType
import com.drdisagree.teledrive.presentation.common.AgeBucket
import com.drdisagree.teledrive.presentation.common.CollectSnackbarMessages
import com.drdisagree.teledrive.presentation.common.Formatters
import com.drdisagree.teledrive.presentation.common.add
import com.drdisagree.teledrive.presentation.components.BottomBarSnackbarHost
import com.drdisagree.teledrive.presentation.components.ConfirmDialog
import com.drdisagree.teledrive.presentation.components.ConnectionDot
import com.drdisagree.teledrive.presentation.components.ConnectionIndicator
import com.drdisagree.teledrive.presentation.components.DriveAvatarSwitcher
import com.drdisagree.teledrive.presentation.components.FileThumbnail
import com.drdisagree.teledrive.presentation.components.GroupedList
import com.drdisagree.teledrive.presentation.components.LoadingState
import com.drdisagree.teledrive.presentation.components.PermissionWarningCard
import com.drdisagree.teledrive.presentation.components.chartColor
import com.drdisagree.teledrive.presentation.components.keyboardCollection
import com.drdisagree.teledrive.presentation.components.keyboardCollectionItem
import com.drdisagree.teledrive.presentation.components.label
import com.drdisagree.teledrive.presentation.components.rememberKeyboardCollection
import com.drdisagree.teledrive.presentation.navigation.LocalBottomBarInset
import com.drdisagree.teledrive.presentation.platform.LocalPermissionRequester
import com.drdisagree.teledrive.presentation.platform.LocalPlatformCapabilities
import com.drdisagree.teledrive.presentation.preview.PreviewSequence
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.home_greeting
import com.drdisagree.teledrive.resources.home_greeting_afternoon
import com.drdisagree.teledrive.resources.home_greeting_dawn
import com.drdisagree.teledrive.resources.home_greeting_evening
import com.drdisagree.teledrive.resources.home_greeting_morning
import androidx.compose.runtime.produceState
import androidx.lifecycle.repeatOnLifecycle
import java.time.ZonedDateTime
import com.drdisagree.teledrive.resources.redesign_transfers
import com.drdisagree.teledrive.resources.app_backed_up
import com.drdisagree.teledrive.resources.app_name
import com.drdisagree.teledrive.resources.backup_age_days
import com.drdisagree.teledrive.resources.backup_age_hours
import com.drdisagree.teledrive.resources.backup_age_minutes
import com.drdisagree.teledrive.resources.collection_offline_on_device
import com.drdisagree.teledrive.resources.common_cancel
import com.drdisagree.teledrive.resources.common_pause
import com.drdisagree.teledrive.resources.common_resume
import com.drdisagree.teledrive.resources.file_count
import com.drdisagree.teledrive.resources.home_active_transfers
import com.drdisagree.teledrive.resources.home_back_up_pending
import com.drdisagree.teledrive.resources.home_backing_up
import com.drdisagree.teledrive.resources.home_backup_all_in_telegram
import com.drdisagree.teledrive.resources.home_backup_at
import com.drdisagree.teledrive.resources.home_backup_auto_off
import com.drdisagree.teledrive.resources.home_backup_just_now
import com.drdisagree.teledrive.resources.home_backup_never
import com.drdisagree.teledrive.resources.home_backup_nothing_to_back_up
import com.drdisagree.teledrive.resources.home_backup_paused
import com.drdisagree.teledrive.resources.home_backup_waiting_charger
import com.drdisagree.teledrive.resources.home_backup_waiting_wifi
import com.drdisagree.teledrive.resources.home_cancel_backup_action
import com.drdisagree.teledrive.resources.home_cancel_backup_title
import com.drdisagree.teledrive.resources.home_choose_folders
import com.drdisagree.teledrive.resources.home_deleted_files_kept_removal
import com.drdisagree.teledrive.resources.home_failed_count
import com.drdisagree.teledrive.resources.home_lock_app
import com.drdisagree.teledrive.resources.home_not_backed_up_count
import com.drdisagree.teledrive.resources.home_open_drives
import com.drdisagree.teledrive.resources.home_queued_uploads_discarded_files
import com.drdisagree.teledrive.resources.home_saving_changes
import com.drdisagree.teledrive.resources.home_saving_changes_left
import com.drdisagree.teledrive.resources.home_saving_folder_changes
import com.drdisagree.teledrive.resources.home_scan_now
import com.drdisagree.teledrive.resources.home_scanning
import com.drdisagree.teledrive.resources.home_section_favorites
import com.drdisagree.teledrive.resources.home_section_recent
import com.drdisagree.teledrive.resources.home_section_storage
import com.drdisagree.teledrive.resources.home_session_progress
import com.drdisagree.teledrive.resources.home_status_connected
import com.drdisagree.teledrive.resources.home_status_connecting
import com.drdisagree.teledrive.resources.home_status_offline
import com.drdisagree.teledrive.resources.home_status_rebuilding
import com.drdisagree.teledrive.resources.home_status_syncing
import com.drdisagree.teledrive.resources.home_storage_percent
import com.drdisagree.teledrive.resources.home_storage_percent_min
import com.drdisagree.teledrive.resources.home_sync_now
import com.drdisagree.teledrive.resources.home_transfer_history
import com.drdisagree.teledrive.resources.home_transfer_history_subtitle
import com.drdisagree.teledrive.resources.home_waiting_count
import com.drdisagree.teledrive.resources.home_trash_summary
import com.drdisagree.teledrive.resources.nav_files
import com.drdisagree.teledrive.resources.trash
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    onOpenFile: (String, PreviewSequence) -> Unit,
    onOpenFolder: (String) -> Unit,
    onOpenTransfers: () -> Unit,
    onOpenChannels: () -> Unit,
    onOpenCollection: (CollectionType) -> Unit,
    onOpenBackupSettings: () -> Unit,
    onOpenFiles: () -> Unit,
    onOpenTrash: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val trashSummary by viewModel.trashSummary.collectAsStateWithLifecycle()
    val personalization by viewModel.personalization.collectAsStateWithLifecycle()
    var lastConnectionStatus by remember { mutableStateOf<ConnectionStatus?>(null) }
    val scanning by viewModel.scanning.collectAsStateWithLifecycle()
    val pendingSync by viewModel.pendingSync.collectAsStateWithLifecycle()
    var shownSync by remember { mutableStateOf(pendingSync) }
    if (pendingSync.visible) shownSync = pendingSync
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val lifecycleOwner = LocalLifecycleOwner.current
    val now by produceState(ZonedDateTime.now(), personalization, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                value = ZonedDateTime.now()
                delay(1_000)
            }
        }
    }
    val permissionRequester = LocalPermissionRequester.current
    val capabilities = LocalPlatformCapabilities.current

    var confirmCancelBackup by remember { mutableStateOf<String?>(null) }
    confirmCancelBackup?.let { sessionId ->
        ConfirmDialog(
            title = stringResource(Res.string.home_cancel_backup_title),
            message = stringResource(Res.string.home_queued_uploads_discarded_files),
            confirmLabel = stringResource(Res.string.home_cancel_backup_action),
            destructive = true,
            onConfirm = {
                confirmCancelBackup = null
                viewModel.cancelBackup(sessionId)
            },
            onDismiss = { confirmCancelBackup = null }
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refreshPermissions()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    CollectSnackbarMessages(viewModel.messages, snackbarHostState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { BottomBarSnackbarHost(snackbarHostState) },
        topBar = {
            LargeFlexibleTopAppBar(
            title = {
                if (scrollBehavior.state.collapsedFraction >= 0.5f) {
                    Text(stringResource(Res.string.app_name))
                }
            },
            expandedHeight = 96.dp,
            subtitle = {
                    val status = rememberConnectionStatus(
                        offline = state.offline,
                        connection = state.connection,
                        known = !state.loading
                    )
                    AnimatedVisibility(
                        visible = status != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        val shown = status ?: lastConnectionStatus
                        if (shown != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ConnectionDot(status = shown.indicator)
                                Spacer(Modifier.width(4.dp))
                                Text(stringResource(shown.labelRes))
                            }
                        }
                    }
                    lastConnectionStatus = status ?: lastConnectionStatus
                },
                actions = {
                    if (state.appLockEnabled) {
                        IconButton(onClick = viewModel::lockNow) {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = stringResource(Res.string.home_lock_app)
                            )
                        }
                    }
                    state.activeChannel?.let { channel ->
                        val driveCount by viewModel.driveCount.collectAsStateWithLifecycle()
                        IconButton(
                            onClick = onOpenChannels,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            DriveAvatarSwitcher(
                                channel = channel,
                                canCycle = driveCount > 1,
                                onCycle = viewModel::cycleDrive,
                                size = AVATAR_SIZE,
                                contentDescription = stringResource(
                                    Res.string.home_open_drives
                                )
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        if (state.loading) {
            LoadingState(modifier = Modifier.padding(padding))
            return@Scaffold
        }
        BoxWithConstraints(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = padding.add(
                    horizontal = if (maxWidth >= 840.dp) 32.dp else 16.dp,
                    top = 8.dp,
                    bottom = 24.dp + LocalBottomBarInset.current
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            if (state.missingPermissions.isNotEmpty()) {
                item(key = "permissions") {
                    Column(Modifier.animateItem()) {
                        PermissionWarningCard(
                            missing = state.missingPermissions,
                            onGrant = {
                                permissionRequester.request(state.missingPermissions) {
                                    viewModel.refreshPermissions()
                                }
                            }
                        )
                    }
                }
            }
            item(key = "drive-overview") {
                DriveOverview(
                    greeting = if (personalization.displayName.isBlank()) {
                        stringResource(Res.string.home_greeting)
                    } else {
                        val greetingResource = when (homeGreetingPeriodForHour(now.hour)) {
                            HomeGreetingPeriod.DAWN -> Res.string.home_greeting_dawn
                            HomeGreetingPeriod.MORNING -> Res.string.home_greeting_morning
                            HomeGreetingPeriod.AFTERNOON -> Res.string.home_greeting_afternoon
                            HomeGreetingPeriod.EVENING -> Res.string.home_greeting_evening
                        }
                        stringResource(greetingResource, personalization.displayName)
                    },
                    dateTime = formatHomeDateTime(now, personalization)
                )
            }
            if (capabilities.supportsAutoBackup) {
                item(key = "backup") {
                    Column(Modifier.animateItem()) {
                        BackupCard(
                            state = state,
                            scanning = scanning,
                            onChooseFolders = onOpenBackupSettings,
                            onScanNow = viewModel::scanNow,
                            onBackUpPending = viewModel::backUpPending,
                            onPause = viewModel::pauseBackup,
                            onResume = viewModel::resumeBackup,
                            onCancel = { confirmCancelBackup = it }
                        )
                    }
                }
            }
            if (pendingSync.visible) {
                item(key = "pending-sync") {
                    Column(Modifier.animateItem()) {
                        PendingSyncCard(pending = shownSync, onSyncNow = viewModel::syncNow)
                    }
                }
            }

            if (state.favoriteFolders.isNotEmpty()) {
                item(key = "favorites") {
                    Column(Modifier.animateItem()) {
                        SectionHeader(stringResource(Res.string.home_section_favorites))
                        Spacer(Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            state.favoriteFolders.take(5).forEach { folder ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable(role = Role.Button) { onOpenFolder(folder.id) }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.Folder,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        folder.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (state.showRecentSection && state.recentFiles.isNotEmpty()) {
                item(key = "recent") {
                    Column(Modifier.animateItem()) {
                        SectionHeader(stringResource(Res.string.home_section_recent))
                        Spacer(Modifier.height(8.dp))
                        val recentState = rememberLazyListState()
                        val recentKeyboard = rememberKeyboardCollection(recentState, horizontal = true)
                        LazyRow(
                            state = recentState,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.keyboardCollection(
                                recentKeyboard,
                                state.recentFiles.size
                            )
                        ) {
                            itemsIndexed(state.recentFiles, key = { _, it -> it.id }) { index, file ->
                                Column(
                                    modifier = Modifier
                                        .keyboardCollectionItem(recentKeyboard, index)
                                        .width(112.dp)
                                        .clip(MaterialTheme.shapes.large)
                                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                        .clickable(role = Role.Button) {
                                            onOpenFile(
                                                file.id,
                                                PreviewSequence(
                                                    sortField = FileSortField.DATE_ADDED,
                                                    sortDirection = SortDirection.DESCENDING
                                                )
                                            )
                                        }
                                        .padding(6.dp)
                                ) {
                                    FileThumbnail(
                                        file = file,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = file.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (state.storage.isNotEmpty()) {
                item(key = "storage") {
                    Column(Modifier.animateItem()) {
                        SectionHeader(stringResource(Res.string.home_section_storage))
                        Spacer(Modifier.height(8.dp))
                        StorageCard(slices = state.storage)
                    }
                }
            }
            item(key = "collections") {
                Column(Modifier.animateItem()) {
                    CollectionLinks(
                        showArchived = state.showArchivedSection,
                        showHidden = state.showHiddenSection,
                        activeTransferCount = state.activeTransferCount,
                        offlineBytes = state.offlineBytes,
                        onOpenCollection = onOpenCollection,
                        onOpenTransfers = onOpenTransfers,
                        showTrash = !capabilities.isDesktop,
                        trashItemCount = trashSummary.itemCount,
                        trashTotalSizeBytes = trashSummary.totalSizeBytes,
                        onOpenTrash = onOpenTrash
                    )
                }
            }
        }
        }
    }
}

@Composable
internal fun DriveOverview(
    greeting: String,
    dateTime: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                greeting,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                dateTime,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CollectionLinks(
    showArchived: Boolean,
    showHidden: Boolean,
    activeTransferCount: Int,
    offlineBytes: Long,
    onOpenCollection: (CollectionType) -> Unit,
    onOpenTransfers: () -> Unit,
    showTrash: Boolean,
    trashItemCount: Int,
    trashTotalSizeBytes: Long,
    onOpenTrash: () -> Unit,
    showTransferHistory: Boolean = true
) {
    GroupedList(horizontalPadding = 0.dp) {
        CollectionType.entries.forEach { collection ->
            val visible = when (collection) {
                CollectionType.ARCHIVED -> showArchived
                CollectionType.HIDDEN -> showHidden
                else -> true
            }
            add(visible = visible) {
                CollectionRow(
                    icon = collection.icon,
                    title = stringResource(collection.titleRes),
                    subtitle = if (
                        collection == CollectionType.AVAILABLE_OFFLINE && offlineBytes > 0
                    ) {
                        stringResource(
                            Res.string.collection_offline_on_device,
                            Formatters.bytes(offlineBytes)
                        )
                    } else {
                        stringResource(collection.subtitleRes)
                    },
                    onClick = { onOpenCollection(collection) }
                )
            }
        }
        if (showTransferHistory) {
            add {
                CollectionRow(
                    icon = Icons.Filled.SwapVert,
                    title = stringResource(Res.string.home_transfer_history),
                    subtitle = if (activeTransferCount > 0) {
                        stringResource(Res.string.home_active_transfers, activeTransferCount)
                    } else {
                        stringResource(Res.string.home_transfer_history_subtitle)
                    },
                    onClick = onOpenTransfers
                )
            }
        }
        if (showTrash) {
            add {
                CollectionRow(
                    icon = Icons.Filled.Delete,
                    title = stringResource(Res.string.trash),
                    subtitle = stringResource(
                        Res.string.home_trash_summary,
                        trashItemCount,
                        Formatters.bytes(trashTotalSizeBytes)
                    ),
                    onClick = onOpenTrash
                )
            }
        }
    }
}

@Composable
private fun PendingSyncCard(pending: PendingSync, onSyncNow: () -> Unit) {
    GroupedList(horizontalPadding = 0.dp) {
        add {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CloudSync,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(Res.string.home_saving_changes),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = if (pending.files > 0) {
                            pluralStringResource(
                                Res.plurals.home_saving_changes_left,
                                pending.files,
                                pending.files
                            )
                        } else {
                            stringResource(Res.string.home_saving_folder_changes)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onSyncNow, shapes = ButtonDefaults.shapes()) {
                    Text(stringResource(Res.string.home_sync_now))
                }
            }
        }
    }
}

@Composable
private fun CollectionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.semantics { heading() }
    )
}

@Composable
private fun DesktopHomeOverviewCard(
    state: HomeUiState,
    storageSlices: List<StorageSlice>,
    onOpenFiles: () -> Unit,
    onOpenTransfers: () -> Unit,
    modifier: Modifier = Modifier
) {
    val connectionStatus = when {
        state.offline -> Res.string.home_status_offline
        state.connection == TelegramConnectionState.READY -> Res.string.home_status_connected
        state.connection == TelegramConnectionState.UPDATING -> Res.string.home_status_syncing
        else -> Res.string.home_status_connecting
    }
    val connectionIndicator = when {
        state.offline -> ConnectionIndicator.OFFLINE
        state.connection == TelegramConnectionState.READY -> ConnectionIndicator.CONNECTED
        else -> ConnectionIndicator.WORKING
    }
    val totalBytes = remember(storageSlices) { storageSlices.sumOf { it.totalBytes } }
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    val detailColor = contentColor.copy(alpha = 0.82f)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val showSideBySide = maxWidth >= 760.dp && totalBytes > 0L
            val donutSize = if (showSideBySide) {
                (maxWidth * 0.19f).coerceIn(144.dp, 224.dp)
            } else {
                (maxWidth * 0.27f).coerceIn(112.dp, 184.dp)
            }
            if (showSideBySide) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DesktopHomeActions(
                        state = state,
                        connectionStatus = connectionStatus,
                        connectionIndicator = connectionIndicator,
                        onOpenFiles = onOpenFiles,
                        onOpenTransfers = onOpenTransfers,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        modifier = Modifier.weight(1.65f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StorageDonutChart(
                            slices = storageSlices,
                            totalBytes = totalBytes,
                            contentColor = contentColor,
                            modifier = Modifier.size(donutSize)
                        )
                        Spacer(Modifier.width(16.dp))
                        StorageLegend(
                            slices = storageSlices,
                            totalBytes = totalBytes,
                            modifier = Modifier.weight(1f),
                            labelColor = contentColor,
                            detailColor = detailColor
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DesktopHomeActions(
                        state = state,
                        connectionStatus = connectionStatus,
                        connectionIndicator = connectionIndicator,
                        onOpenFiles = onOpenFiles,
                        onOpenTransfers = onOpenTransfers
                    )
                    if (totalBytes > 0L) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StorageDonutChart(
                                slices = storageSlices,
                                totalBytes = totalBytes,
                                contentColor = contentColor,
                                modifier = Modifier.size(donutSize)
                            )
                            Spacer(Modifier.width(12.dp))
                            StorageLegend(
                                slices = storageSlices,
                                totalBytes = totalBytes,
                                modifier = Modifier.weight(1f),
                                labelColor = contentColor,
                                detailColor = detailColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopHomeActions(
    state: HomeUiState,
    connectionStatus: org.jetbrains.compose.resources.StringResource,
    connectionIndicator: ConnectionIndicator,
    onOpenFiles: () -> Unit,
    onOpenTransfers: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ConnectionDot(status = connectionIndicator)
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(connectionStatus),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = pluralStringResource(Res.plurals.file_count, state.totalFiles, state.totalFiles),
            style = MaterialTheme.typography.titleLargeEmphasized,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onOpenFiles,
                shapes = ButtonDefaults.shapes(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) { Text(stringResource(Res.string.nav_files)) }
            OutlinedButton(
                onClick = onOpenTransfers,
                shapes = ButtonDefaults.shapes(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                )
            ) { Text(stringResource(Res.string.home_transfer_history)) }
        }
    }
}

@Composable
private fun StorageDonutChart(
    slices: List<StorageSlice>,
    totalBytes: Long,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val sliceColors = slices.map { it.category.chartColor() }
    val sweepAngles = remember(slices) {
        val populated = slices.filter { it.fileCount > 0 }
        val bytes = populated.sumOf { it.totalBytes.coerceAtLeast(0L).toDouble() }
        // Reserve a small visible arc for every nonempty category, then distribute
        // the remaining angle by actual bytes. Legend percentages stay unchanged.
        val minimumAngle = if (populated.isEmpty()) 0f else minOf(5.4f, 360f / populated.size)
        val remainingAngle = (360f - minimumAngle * populated.size).coerceAtLeast(0f)
        slices.map { slice ->
            when {
                slice.fileCount <= 0 -> 0f
                bytes <= 0.0 -> 360f / populated.size
                else -> minimumAngle +
                    (slice.totalBytes.coerceAtLeast(0L).toDouble() / bytes * remainingAngle).toFloat()
            }
        }
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize().padding(7.dp)) {
            val ringWidth = 16.dp.toPx()
            val diameter = (minOf(size.width, size.height) - ringWidth).coerceAtLeast(0f)
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset(
                x = (size.width - diameter) / 2f,
                y = (size.height - diameter) / 2f
            )
            drawArc(
                color = contentColor.copy(alpha = 0.18f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = ringWidth)
            )
            var startAngle = -90f
            slices.forEachIndexed { index, _ ->
                val sweepAngle = sweepAngles[index]
                if (sweepAngle > 0f) {
                    drawArc(
                        color = sliceColors[index],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = ringWidth)
                    )
                }
                startAngle += sweepAngle
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.home_section_storage),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.8f)
            )
            Text(
                text = Formatters.bytes(totalBytes),
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BackupCard(
    state: HomeUiState,
    scanning: Boolean,
    onChooseFolders: () -> Unit,
    onScanNow: () -> Unit,
    onBackUpPending: () -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onCancel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        val statusIcon = when {
            state.rebuilding -> Icons.Filled.CloudSync
            state.failedCount > 0 -> Icons.Outlined.ErrorOutline
            state.activeBackup?.status == BackupSessionStatus.PAUSED -> Icons.Filled.CloudSync
            state.pendingCount > 0 -> Icons.Filled.CloudUpload
            state.offline -> Icons.Filled.CloudOff
            else -> Icons.Filled.CloudDone
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialShapes.Cookie9Sided.toShape())
                        .background(
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
                        )
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = when {
                            state.activeBackup?.status == BackupSessionStatus.RUNNING ->
                                stringResource(
                                    when (state.backupHold) {
                                        BackupHold.CHARGER -> Res.string.home_backup_waiting_charger
                                        BackupHold.WIFI -> Res.string.home_backup_waiting_wifi
                                        null -> Res.string.home_backing_up
                                    }
                                )

                            state.activeBackup?.status == BackupSessionStatus.PAUSED ->
                                stringResource(Res.string.home_backup_paused)

                            state.rebuilding -> stringResource(Res.string.home_status_rebuilding)
                            state.totalFiles == 0 ->
                                stringResource(Res.string.home_backup_nothing_to_back_up)

                            state.failedCount > 0 ->
                                stringResource(Res.string.home_failed_count, state.failedCount)

                            state.pendingCount > 0 ->
                                stringResource(Res.string.home_waiting_count, state.pendingCount)

                            state.backedUpCount < state.totalFiles -> stringResource(
                                Res.string.home_not_backed_up_count,
                                state.totalFiles - state.backedUpCount
                            )

                            else -> stringResource(Res.string.app_backed_up)
                        },
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = backupFreshnessLabel(
                            autoBackupEnabled = state.autoBackupEnabled,
                            lastBackupAt = state.lastBackupAt,
                            totalFiles = state.totalFiles,
                            backedUpCount = state.backedUpCount
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            var lastSession by remember { mutableStateOf(state.activeBackup) }
            state.activeBackup?.let { lastSession = it }
            AnimatedVisibility(
                visible = state.activeBackup != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                lastSession?.let { session ->
                    Column {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val sessionButtonColors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentColor = MaterialTheme.colorScheme.primaryContainer
                            )
                            if (session.status == BackupSessionStatus.PAUSED) {
                                Button(
                                    onClick = { onResume(session.id) },
                                    shapes = ButtonDefaults.shapes(),
                                    colors = sessionButtonColors
                                ) { Text(stringResource(Res.string.common_resume)) }
                            } else {
                                Button(
                                    onClick = { onPause(session.id) },
                                    shapes = ButtonDefaults.shapes(),
                                    colors = sessionButtonColors
                                ) { Text(stringResource(Res.string.common_pause)) }
                            }
                            OutlinedButton(
                                onClick = { onCancel(session.id) },
                                shapes = ButtonDefaults.shapes(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                        .copy(alpha = 0.5f)
                                )
                            ) { Text(stringResource(Res.string.common_cancel)) }
                        }
                        Spacer(Modifier.height(12.dp))
                        LinearWavyProgressIndicator(
                            progress = { session.progress },
                            amplitude = {
                                if (session.status == BackupSessionStatus.RUNNING) 1f else 0f
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(
                                Res.string.home_session_progress,
                                session.completedFiles,
                                session.totalFiles
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = state.rebuilding && state.activeBackup == null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    LinearWavyProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
            AnimatedVisibility(
                visible = state.activeBackup == null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    val foldersSelected = state.backupFoldersSelected
                    val pending = state.localOnlyCount
                    Button(
                        onClick = when {
                            pending > 0 -> onBackUpPending
                            foldersSelected -> onScanNow
                            else -> onChooseFolders
                        },
                        shapes = ButtonDefaults.shapes(),
                        enabled = !scanning,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            when {
                                scanning -> stringResource(Res.string.home_scanning)
                                pending > 0 -> pluralStringResource(
                                    Res.plurals.home_back_up_pending,
                                    pending,
                                    pending
                                )

                                foldersSelected -> stringResource(Res.string.home_scan_now)
                                else -> stringResource(Res.string.home_choose_folders)
                            }
                        )
                    }
                }
            }
        }
    }
}

/** Only shown when connectivity needs attention; a recovery shows briefly, then the row goes. */
@Composable
private fun rememberConnectionStatus(
    offline: Boolean,
    connection: TelegramConnectionState,
    known: Boolean
): ConnectionStatus? {
    if (!known) return null
    val settled = !offline && connection == TelegramConnectionState.READY
    var wasSettled by rememberSaveable { mutableStateOf(settled) }
    var showRecovered by remember { mutableStateOf(false) }

    LaunchedEffect(settled) {
        if (settled && !wasSettled) {
            showRecovered = true
            delay(RECOVERED_VISIBLE_MS.milliseconds)
            showRecovered = false
        }
        wasSettled = settled
    }

    return when {
        offline -> ConnectionStatus(
            ConnectionIndicator.OFFLINE,
            Res.string.home_status_offline
        )

        connection == TelegramConnectionState.UPDATING -> ConnectionStatus(
            ConnectionIndicator.WORKING,
            Res.string.home_status_syncing
        )

        connection != TelegramConnectionState.READY -> ConnectionStatus(
            ConnectionIndicator.WORKING,
            Res.string.home_status_connecting
        )

        showRecovered -> ConnectionStatus(
            ConnectionIndicator.CONNECTED,
            Res.string.home_status_connected
        )

        else -> null
    }
}

@Composable
private fun StorageCard(
    slices: List<StorageSlice>,
    modifier: Modifier = Modifier,
    showFileCount: Boolean = true
) {
    val totalBytes = remember(slices) { slices.sumOf { it.totalBytes } }
    val totalFiles = remember(slices) { slices.sumOf { it.fileCount } }
    val isDesktop = LocalPlatformCapabilities.current.isDesktop
    val animatedTotalFiles by animateIntAsState(
        targetValue = totalFiles,
        animationSpec = if (isDesktop) tween(420, easing = FastOutSlowInEasing) else snap(),
        label = "storage-file-count"
    )
    if (totalBytes <= 0L) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = Formatters.bytes(totalBytes),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (showFileCount) {
                Text(
                    text = pluralStringResource(
                        Res.plurals.file_count,
                        if (isDesktop) animatedTotalFiles else totalFiles,
                        if (isDesktop) animatedTotalFiles else totalFiles
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(16.dp))
            StorageBar(slices = slices, totalBytes = totalBytes)
            Spacer(Modifier.height(16.dp))
            StorageLegend(slices = slices, totalBytes = totalBytes)
        }
    }
}

/** Every slice keeps a sliver, so nothing vanishes entirely. */
@Composable
private fun StorageBar(slices: List<StorageSlice>, totalBytes: Long) {
    val isDesktop = LocalPlatformCapabilities.current.isDesktop
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(BAR_HEIGHT),
        horizontalArrangement = Arrangement.spacedBy(BAR_GAP)
    ) {
        slices.forEach { slice ->
            val targetFraction = (slice.totalBytes.toDouble() / totalBytes.toDouble())
                .toFloat()
                .coerceAtLeast(MIN_SLICE)
            val fraction by animateFloatAsState(
                targetValue = targetFraction,
                animationSpec = if (isDesktop) tween(500, easing = FastOutSlowInEasing) else snap(),
                label = "storage-${slice.category}-bar"
            )
            Box(
                modifier = Modifier
                    .weight(fraction)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(slice.category.chartColor())
            )
        }
    }
}

@Composable
private fun StorageLegend(
    slices: List<StorageSlice>,
    totalBytes: Long,
    modifier: Modifier = Modifier,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    detailColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        slices.forEachIndexed { index, slice ->
            val percent = slice.totalBytes * 100f / totalBytes
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(LEGEND_DOT)
                        .clip(CircleShape)
                        .background(slice.category.chartColor())
                )
                Spacer(Modifier.width(8.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = slice.category.label(),
                        style = MaterialTheme.typography.bodySmall,
                        color = labelColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = Formatters.bytes(slice.totalBytes),
                        style = MaterialTheme.typography.bodySmall,
                        color = detailColor,
                        maxLines = 1
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = if (percent < 1f) {
                        stringResource(Res.string.home_storage_percent_min)
                    } else {
                        stringResource(Res.string.home_storage_percent, percent.roundToInt())
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = detailColor,
                    maxLines = 1
                )
            }
            if (index < slices.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(detailColor.copy(alpha = 0.12f))
                )
            }
        }
    }
}

/**
 * A disabled schedule outranks a stale timestamp, which means nothing while nothing is scheduled.
 */
@Composable
private fun backupFreshnessLabel(
    autoBackupEnabled: Boolean,
    lastBackupAt: Long?,
    totalFiles: Int,
    backedUpCount: Int
): String = when {
    !autoBackupEnabled -> stringResource(Res.string.home_backup_auto_off)
    /* Files indexed from an existing channel are already safe even though this
       device never ran a session, so claiming "no backup yet" would be wrong. */
    lastBackupAt == null && totalFiles > 0 && backedUpCount >= totalFiles ->
        stringResource(Res.string.home_backup_all_in_telegram)

    lastBackupAt == null -> stringResource(Res.string.home_backup_never)
    else -> when (val age = Formatters.relativeAge(lastBackupAt)) {
        AgeBucket.JustNow -> stringResource(Res.string.home_backup_just_now)
        is AgeBucket.Minutes -> stringResource(
            Res.string.home_backup_at,
            pluralStringResource(Res.plurals.backup_age_minutes, age.value, age.value)
        )

        is AgeBucket.Hours -> stringResource(
            Res.string.home_backup_at,
            pluralStringResource(Res.plurals.backup_age_hours, age.value, age.value)
        )

        is AgeBucket.Days -> stringResource(
            Res.string.home_backup_at,
            pluralStringResource(Res.plurals.backup_age_days, age.value, age.value)
        )

        is AgeBucket.Longer -> stringResource(
            Res.string.home_backup_at,
            Formatters.date(age.epochMillis)
        )
    }
}

private val AVATAR_SIZE = 32.dp
private const val RECOVERED_VISIBLE_MS = 2_000L
private val BAR_HEIGHT = 14.dp
private val BAR_GAP = 3.dp
private val LEGEND_DOT = 12.dp
private const val MIN_SLICE = 0.02f
