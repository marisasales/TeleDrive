package com.drdisagree.teledrive.core.media

import java.io.File

interface ThumbnailStore {

    suspend fun thumbnailBytes(fileId: String, generateIfMissing: Boolean = true): ByteArray?

    suspend fun uploadThumbnailFile(fileId: String): File?
}
