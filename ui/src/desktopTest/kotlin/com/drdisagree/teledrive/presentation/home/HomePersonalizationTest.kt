package com.drdisagree.teledrive.presentation.home

import com.drdisagree.teledrive.domain.model.AppLanguage
import java.time.ZonedDateTime
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomePersonalizationTest {
    private val english = HomePersonalization(language = AppLanguage.ENGLISH)

    @Test
    fun midnightAndNoonRespectClockFormat() {
        val midnight = ZonedDateTime.parse("2026-10-25T00:18:00-03:00")
        assertTrue(formatHomeDateTime(midnight, english).endsWith("00:18"))
        assertTrue(formatHomeDateTime(midnight, english.copy(use24HourClock = false)).endsWith("12:18 AM"))
        val noon = midnight.withHour(12)
        assertTrue(formatHomeDateTime(noon, english).endsWith("12:18"))
        assertTrue(formatHomeDateTime(noon, english.copy(use24HourClock = false)).endsWith("12:18 PM"))
    }

    @Test
    fun greetingPeriodFollowsLocalHour() {
        assertEquals(HomeGreetingPeriod.DAWN, homeGreetingPeriodForHour(0))
        assertEquals(HomeGreetingPeriod.DAWN, homeGreetingPeriodForHour(4))
        assertEquals(HomeGreetingPeriod.MORNING, homeGreetingPeriodForHour(5))
        assertEquals(HomeGreetingPeriod.MORNING, homeGreetingPeriodForHour(11))
        assertEquals(HomeGreetingPeriod.AFTERNOON, homeGreetingPeriodForHour(12))
        assertEquals(HomeGreetingPeriod.AFTERNOON, homeGreetingPeriodForHour(17))
        assertEquals(HomeGreetingPeriod.EVENING, homeGreetingPeriodForHour(18))
        assertEquals(HomeGreetingPeriod.EVENING, homeGreetingPeriodForHour(23))
    }

    @Test
    fun portugueseDateUsesConfiguredLanguage() {
        val date = ZonedDateTime.parse("2026-10-25T13:18:00-03:00")
        val formatted = formatHomeDateTime(date, english.copy(language = AppLanguage.PORTUGUESE_BR))
        assertTrue(formatted.startsWith("Domingo, 25 de out"))
        assertTrue(formatted.endsWith("13:18"))
    }

    @Test
    fun portugueseDateDoesNotDependOnJvmDefaultLocale() {
        val originalLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.ENGLISH)
            val date = ZonedDateTime.parse("2026-10-25T13:18:00-03:00")
            val formatted = formatHomeDateTime(date, english.copy(language = AppLanguage.PORTUGUESE_BR))

            assertTrue(formatted.startsWith("Domingo, 25 de out"))
            assertTrue(formatted.endsWith("13:18"))
        } finally {
            Locale.setDefault(originalLocale)
        }
    }

    @Test
    fun dateAndTimeFollowProvidedLocalZoneAcrossMidnight() {
        val local = ZonedDateTime.parse("2026-10-25T01:18:00Z")
            .withZoneSameInstant(java.time.ZoneId.of("America/Sao_Paulo"))
        val formatted = formatHomeDateTime(local, english)
        assertTrue(formatted.startsWith("Saturday, Oct 24"))
        assertTrue(formatted.endsWith("22:18"))
    }
}
