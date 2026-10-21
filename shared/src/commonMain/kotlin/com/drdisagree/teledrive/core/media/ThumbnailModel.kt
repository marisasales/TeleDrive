package com.drdisagree.teledrive.core.media

data class ThumbnailModel(val fileId: String, val cacheOnly: Boolean = false)

fun thumbnailCacheKey(fileId: String): String = "thumb:$fileId"
