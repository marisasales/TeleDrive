package com.drdisagree.teledrive.domain.model

enum class FileTileSize(
    val gridSize: Int,
    val fileSpan: Int,
    val tileWidthDp: Int,
    val thumbnailAspectRatio: Float
) {
    LARGE(gridSize = 3, fileSpan = 4, tileWidthDp = 222, thumbnailAspectRatio = 1f),
    MEDIUM(gridSize = 4, fileSpan = 3, tileWidthDp = 164, thumbnailAspectRatio = 1.2f),
    SMALL(gridSize = 5, fileSpan = 2, tileWidthDp = 106, thumbnailAspectRatio = 1.45f);

    companion object {
        fun fromGridSize(gridSize: Int): FileTileSize = when {
            gridSize < MEDIUM.gridSize -> LARGE
            gridSize == MEDIUM.gridSize -> MEDIUM
            else -> SMALL
        }
    }
}
