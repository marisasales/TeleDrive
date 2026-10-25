package com.drdisagree.teledrive.presentation.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeGreetingPeriodTest {

    @Test
    fun greetingPeriodMatchesLocalHourBoundaries() {
        assertEquals(HomeGreetingPeriod.DAWN, homeGreetingPeriodForHour(0))
        assertEquals(HomeGreetingPeriod.DAWN, homeGreetingPeriodForHour(4))
        assertEquals(HomeGreetingPeriod.MORNING, homeGreetingPeriodForHour(5))
        assertEquals(HomeGreetingPeriod.MORNING, homeGreetingPeriodForHour(11))
        assertEquals(HomeGreetingPeriod.AFTERNOON, homeGreetingPeriodForHour(12))
        assertEquals(HomeGreetingPeriod.AFTERNOON, homeGreetingPeriodForHour(17))
        assertEquals(HomeGreetingPeriod.EVENING, homeGreetingPeriodForHour(18))
        assertEquals(HomeGreetingPeriod.EVENING, homeGreetingPeriodForHour(23))
    }
}
