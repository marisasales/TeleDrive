package com.drdisagree.teledrive.core.files

/**
 * Image formats the thumbnail pipeline can actually decode (desktop ImageIO and
 * Android BitmapFactory). Gallery uses this allow-list so formats such as SVG,
 * HEIC/HEIF, AVIF, TIFF and camera RAW do not show up as broken image tiles.
 */
object ImageFormats {

    val displayableExtensions: List<String> = listOf(
        "jpg",
        "jpeg",
        "png",
        "webp",
        "gif",
        "bmp"
    )
}
