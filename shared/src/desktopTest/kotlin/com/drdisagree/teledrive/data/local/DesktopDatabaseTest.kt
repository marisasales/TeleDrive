package com.drdisagree.teledrive.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.drdisagree.teledrive.core.telegram.TelegramProxyType
import com.drdisagree.teledrive.data.local.database.TeleDriveDatabase
import com.drdisagree.teledrive.data.local.entity.FileEntity
import com.drdisagree.teledrive.data.local.entity.ProxyEntity
import com.drdisagree.teledrive.domain.model.BackupState
import com.drdisagree.teledrive.domain.model.FileCategory
import com.drdisagree.teledrive.domain.model.FileQuerySpec
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DesktopDatabaseTest {

    private fun openDatabase(file: File): TeleDriveDatabase =
        Room.databaseBuilder<TeleDriveDatabase>(name = file.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

    @Test
    fun `proxy row survives a round trip`() = runBlocking {
        val file = File.createTempFile("teledrive", ".db")
        val database = openDatabase(file)
        try {
            val proxy = ProxyEntity(
                id = "p1",
                label = "Test",
                type = TelegramProxyType.SOCKS5,
                host = "127.0.0.1",
                port = 1080,
                addedAt = 42L
            )
            database.proxyDao().upsert(proxy)
            assertEquals(listOf(proxy), database.proxyDao().all())
        } finally {
            database.close()
            file.delete()
        }
    }

    @Test
    fun `raw file query runs against an empty database`() = runBlocking {
        val file = File.createTempFile("teledrive", ".db")
        val database = openDatabase(file)
        try {
            val ids = database.fileDao().idList(FileQueryBuilder.buildIds(FileQuerySpec()))
            assertTrue(ids.isEmpty())
        } finally {
            database.close()
            file.delete()
        }
    }

    @Test
    fun `limited file query binds limit and returns deterministic sample`() = runBlocking {
        val file = File.createTempFile("teledrive", ".db")
        val database = openDatabase(file)
        try {
            listOf("c", "a", "b").forEach { database.fileDao().upsert(fileEntity(it)) }

            assertEquals(
                listOf("a", "b"),
                database.fileDao().idList(FileQueryBuilder.buildLimited(FileQuerySpec(), 2))
            )
            assertEquals(
                listOf("a"),
                database.fileDao().idList(FileQueryBuilder.buildLimited(FileQuerySpec(), 0))
            )
        } finally {
            database.close()
            file.delete()
        }
    }

    private fun fileEntity(id: String) = FileEntity(
        id = id,
        folderId = null,
        name = "$id.txt",
        sizeBytes = 1,
        mimeType = "text/plain",
        category = FileCategory.DOCUMENT,
        localPath = null,
        contentHash = null,
        chatId = null,
        messageId = null,
        remoteFileId = null,
        remoteUniqueId = null,
        backupState = BackupState.BACKED_UP,
        createdAt = 1,
        modifiedAt = 1,
        addedAt = 1
    )
}
