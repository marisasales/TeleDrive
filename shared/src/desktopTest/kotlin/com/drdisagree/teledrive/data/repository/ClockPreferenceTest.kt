package com.drdisagree.teledrive.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.drdisagree.teledrive.core.crypto.CredentialCipher
import com.drdisagree.teledrive.data.local.preferences.PreferenceKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class ClockPreferenceTest {
    private class MemoryStore : DataStore<Preferences> {
        override val data = MutableStateFlow(emptyPreferences())
        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
            transform(data.value).also { data.value = it }
    }

    private val unusedCipher = object : CredentialCipher {
        override fun encrypt(plaintext: ByteArray): ByteArray = error("Not used for clock preferences")
        override fun decrypt(ciphertext: ByteArray): ByteArray = error("Not used for clock preferences")
    }

    @Test
    fun clockDefaultsAndRoundTripsWithoutChangingColumns() = runBlocking {
        val store = MemoryStore()
        val repository = SettingsRepositoryImpl(store, unusedCipher)
        assertTrue(repository.preferences.first().use24HourClock)
        repository.update { it.copy(use24HourClock = false, gridSize = 5, galleryGridSize = 4) }
        assertEquals(false, store.data.value[PreferenceKeys.USE_24_HOUR_CLOCK])
        val reloaded = SettingsRepositoryImpl(store, unusedCipher)
        assertFalse(reloaded.preferences.first().use24HourClock)
        assertEquals(5, reloaded.preferences.first().gridSize)
        assertEquals(4, reloaded.preferences.first().galleryGridSize)
        reloaded.update { it.copy(use24HourClock = true) }
        assertTrue(repository.preferences.first().use24HourClock)
    }
}
