package com.drdisagree.teledrive.desktop.ui

import java.io.File
import java.util.Base64

/** Uses Windows' common file dialog through the WinForms wrapper available in Windows PowerShell. */
internal object WindowsModernFilePicker {

    private const val OUTPUT_PREFIX = "TELEDRIVE_PATH:"

    fun pickFiles(multiple: Boolean): List<String>? {
        if (!System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
            return null
        }

        val dollar = "$"
        val script = """
            ${dollar}ErrorActionPreference = 'Stop'
            Add-Type -AssemblyName System.Windows.Forms
            ${dollar}dialog = New-Object System.Windows.Forms.OpenFileDialog
            try {
                ${dollar}dialog.AutoUpgradeEnabled = ${dollar}true
                ${dollar}dialog.Multiselect = ${multiple.toString().lowercase()}
                if (${dollar}dialog.ShowDialog() -eq [System.Windows.Forms.DialogResult]::OK) {
                    foreach (${dollar}path in ${dollar}dialog.FileNames) {
                        [Console]::Out.WriteLine('$OUTPUT_PREFIX' + [Convert]::ToBase64String([System.Text.Encoding]::Unicode.GetBytes(${dollar}path)))
                    }
                }
            } finally {
                ${dollar}dialog.Dispose()
            }
        """.trimIndent()
        val powershell = System.getenv("WINDIR")
            ?.let { File(it, "System32/WindowsPowerShell/v1.0/powershell.exe") }
            ?.takeIf(File::isFile)
            ?.absolutePath
            ?: "powershell.exe"

        var process: Process? = null
        return try {
            process = ProcessBuilder(
                powershell,
                "-NoLogo",
                "-NoProfile",
                "-NonInteractive",
                "-STA",
                "-WindowStyle",
                "Hidden",
                "-Command",
                script
            ).redirectErrorStream(true).start()

            val output = process.inputStream.bufferedReader(Charsets.UTF_8).use { it.readLines() }
            if (process.waitFor() != 0) return null

            output.mapNotNull { line ->
                val encodedPath = line.takeIf { it.startsWith(OUTPUT_PREFIX) }
                    ?.removePrefix(OUTPUT_PREFIX)
                    ?: return@mapNotNull null
                runCatching {
                    String(Base64.getDecoder().decode(encodedPath), Charsets.UTF_16LE)
                }.getOrNull()
            }
        } catch (interrupted: InterruptedException) {
            process?.destroyForcibly()
            Thread.currentThread().interrupt()
            null
        } catch (_: Exception) {
            process?.destroyForcibly()
            null
        }
    }
}
