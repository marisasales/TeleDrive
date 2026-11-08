package com.drdisagree.teledrive.core.files

object MimeTypes {

    const val GENERIC = "application/octet-stream"

    /**
     * Extensions the viewer opens as text even when the platform cannot name their MIME type.
     * Without this a code file would fall through to [GENERIC] and offer only a download.
     */
    private val CODE_EXTENSIONS = setOf(
        "txt", "text", "log", "csv", "tsv", "ini", "cfg", "conf", "properties", "env",
        "py", "pyw", "js", "mjs", "cjs", "jsx", "ts", "tsx", "kt", "kts", "java",
        "c", "h", "cpp", "cc", "cxx", "hpp", "hxx", "cs", "go", "rs", "swift", "dart",
        "php", "rb", "pl", "lua", "r", "m", "mm", "scala", "groovy", "gradle",
        "sh", "bash", "zsh", "fish", "ps1", "bat", "cmd",
        "sql", "css", "scss", "sass", "less", "html", "htm", "xhtml", "xml", "svg",
        "json", "jsonc", "json5", "yml", "yaml", "toml", "md", "markdown", "rst",
        "tex", "diff", "patch", "gitignore", "dockerfile", "makefile"
    )

    fun fromFileName(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        if (extension.isEmpty()) {
            // A few dotfiles carry their whole name as the extension.
            val name = fileName.lowercase()
            return if (name in setOf("dockerfile", "makefile", ".gitignore", ".env")) {
                "text/plain"
            } else {
                GENERIC
            }
        }
        val platform = platformMimeTypeFromExtension(extension)
        if (platform != null && platform != GENERIC) return platform
        return when {
            extension in CODE_EXTENSIONS -> when (extension) {
                "md", "markdown" -> "text/markdown"
                "json", "jsonc", "json5" -> "application/json"
                "yml", "yaml" -> "application/yaml"
                "xml", "svg", "xhtml" -> "application/xml"
                else -> "text/plain"
            }

            extension == "mkv" -> "video/x-matroska"
            extension == "flac" -> "audio/flac"
            extension == "opus" -> "audio/opus"
            extension == "7z" -> "application/x-7z-compressed"
            extension == "rar" -> "application/vnd.rar"
            extension == "heic" -> "image/heic"
            extension == "heif" -> "image/heif"
            extension == "apk" -> "application/vnd.android.package-archive"
            else -> GENERIC
        }
    }

    fun isImage(mimeType: String): Boolean = mimeType.startsWith("image/")

    fun isVideo(mimeType: String): Boolean = mimeType.startsWith("video/")

    fun isAudio(mimeType: String): Boolean = mimeType.startsWith("audio/")

    fun isPdf(mimeType: String): Boolean = mimeType == "application/pdf"

    fun isMarkdown(mimeType: String): Boolean = mimeType == "text/markdown"

    fun isApk(mimeType: String, fileName: String? = null): Boolean =
        mimeType == "application/vnd.android.package-archive" ||
                fileName?.lowercase()?.endsWith(".apk") == true

    fun isText(mimeType: String, fileName: String? = null): Boolean =
        mimeType.startsWith("text/") ||
                mimeType == "application/json" ||
                mimeType == "application/xml" ||
                mimeType == "application/yaml" ||
                mimeType == "application/javascript" ||
                mimeType == "application/x-sh" ||
                isCodeFile(fileName)

    fun isCodeFile(fileName: String?): Boolean {
        if (fileName == null) return false
        val lower = fileName.lowercase()
        val extension = lower.substringAfterLast('.', "")
        if (extension.isNotEmpty()) return extension in CODE_EXTENSIONS
        return lower in setOf("dockerfile", "makefile", ".gitignore", ".env")
    }

    fun isArchive(mimeType: String): Boolean = mimeType in setOf(
        "application/zip",
        "application/x-7z-compressed",
        "application/vnd.rar",
        "application/x-tar",
        "application/gzip"
    )
}
