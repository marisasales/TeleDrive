package com.drdisagree.teledrive.presentation.home

import com.drdisagree.teledrive.domain.model.AppLanguage
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HomePersonalization(
    val displayName: String = "",
    val use24HourClock: Boolean = true,
    val language: AppLanguage = AppLanguage.SYSTEM
)

internal enum class HomeGreetingPeriod { DAWN, MORNING, AFTERNOON, EVENING }

internal fun homeGreetingPeriodForHour(hour: Int): HomeGreetingPeriod = when (hour) {
    in 0..4 -> HomeGreetingPeriod.DAWN
    in 5..11 -> HomeGreetingPeriod.MORNING
    in 12..17 -> HomeGreetingPeriod.AFTERNOON
    else -> HomeGreetingPeriod.EVENING
}

internal fun formatHomeDateTime(now: ZonedDateTime, personalization: HomePersonalization): String {
    val locale = when (personalization.language) {
        AppLanguage.SYSTEM -> Locale.getDefault()
        AppLanguage.ENGLISH -> Locale.ENGLISH
        AppLanguage.RUSSIAN -> Locale.forLanguageTag("ru-RU")
        AppLanguage.PORTUGUESE_BR -> Locale.forLanguageTag("pt-BR")
        AppLanguage.CHINESE_SIMPLIFIED -> Locale.forLanguageTag("zh-CN")
    }
    val timePattern = if (personalization.use24HourClock) "HH:mm" else "hh:mm a"
    val date = if (personalization.language == AppLanguage.PORTUGUESE_BR) {
        val weekdays = listOf(
            "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira",
            "sexta-feira", "sábado", "domingo"
        )
        val months = listOf(
            "jan.", "fev.", "mar.", "abr.", "mai.", "jun.",
            "jul.", "ago.", "set.", "out.", "nov.", "dez."
        )
        "${weekdays[now.dayOfWeek.value - 1]}, ${now.dayOfMonth} de ${months[now.monthValue - 1]}"
    } else {
        val datePattern = when (locale.language) {
            "zh" -> "M月d日 EEEE"
            "ru" -> "EEEE, d MMM"
            else -> "EEEE, MMM d"
        }
        now.format(DateTimeFormatter.ofPattern(datePattern, locale))
    }
    val time = now.format(DateTimeFormatter.ofPattern(timePattern, locale))
    return "$date · $time"
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}
