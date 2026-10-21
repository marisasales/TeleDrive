package com.drdisagree.teledrive.presentation.trash

import com.drdisagree.teledrive.domain.model.TrashItem
import com.drdisagree.teledrive.presentation.common.UiText

data class TrashUiState(
    val items: List<TrashItem> = emptyList(),
    val rows: List<TrashRow> = emptyList(),
    val selection: Set<String> = emptySet(),
    val autoClearDays: Int = 30,
    val working: UiText? = null,
    val loading: Boolean = true,
    val totalSizeBytes: Long = 0L
) {
    val selectionMode: Boolean get() = selection.isNotEmpty()
}
