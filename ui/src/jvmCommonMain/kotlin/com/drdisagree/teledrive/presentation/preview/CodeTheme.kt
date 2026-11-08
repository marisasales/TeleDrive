package com.drdisagree.teledrive.presentation.preview

import androidx.compose.ui.graphics.Color

/**
 * Palettes for the code viewer. The key is persisted in preferences, so renaming an entry would
 * reset the user's choice.
 */
enum class CodeTheme(
    val key: String,
    val displayName: String,
    val isDark: Boolean,
    val background: Color,
    val foreground: Color,
    val keyword: Color,
    val string: Color,
    val number: Color,
    val comment: Color,
    val function: Color,
    val type: Color,
    val punctuation: Color,
    val property: Color,
    val heading: Color,
    val link: Color
) {
    AUTO(
        key = "auto",
        displayName = "Automatic",
        isDark = true,
        background = Color(0xFF282A36),
        foreground = Color(0xFFF8F8F2),
        keyword = Color(0xFFFF79C6),
        string = Color(0xFFF1FA8C),
        number = Color(0xFFBD93F9),
        comment = Color(0xFF6272A4),
        function = Color(0xFF50FA7B),
        type = Color(0xFF8BE9FD),
        punctuation = Color(0xFFF8F8F2),
        property = Color(0xFFFFB86C),
        heading = Color(0xFFBD93F9),
        link = Color(0xFF8BE9FD)
    ),
    DRACULA(
        key = "dracula",
        displayName = "Dracula",
        isDark = true,
        background = Color(0xFF282A36),
        foreground = Color(0xFFF8F8F2),
        keyword = Color(0xFFFF79C6),
        string = Color(0xFFF1FA8C),
        number = Color(0xFFBD93F9),
        comment = Color(0xFF6272A4),
        function = Color(0xFF50FA7B),
        type = Color(0xFF8BE9FD),
        punctuation = Color(0xFFF8F8F2),
        property = Color(0xFFFFB86C),
        heading = Color(0xFFBD93F9),
        link = Color(0xFF8BE9FD)
    ),
    MONOKAI(
        key = "monokai",
        displayName = "Monokai",
        isDark = true,
        background = Color(0xFF272822),
        foreground = Color(0xFFF8F8F2),
        keyword = Color(0xFFF92672),
        string = Color(0xFFE6DB74),
        number = Color(0xFFAE81FF),
        comment = Color(0xFF75715E),
        function = Color(0xFFA6E22E),
        type = Color(0xFF66D9EF),
        punctuation = Color(0xFFF8F8F2),
        property = Color(0xFFFD971F),
        heading = Color(0xFFF92672),
        link = Color(0xFF66D9EF)
    ),
    ONE_DARK(
        key = "one_dark",
        displayName = "One Dark",
        isDark = true,
        background = Color(0xFF282C34),
        foreground = Color(0xFFABB2BF),
        keyword = Color(0xFFC678DD),
        string = Color(0xFF98C379),
        number = Color(0xFFD19A66),
        comment = Color(0xFF5C6370),
        function = Color(0xFF61AFEF),
        type = Color(0xFFE5C07B),
        punctuation = Color(0xFFABB2BF),
        property = Color(0xFFE06C75),
        heading = Color(0xFFE06C75),
        link = Color(0xFF56B6C2)
    ),
    NORD(
        key = "nord",
        displayName = "Nord",
        isDark = true,
        background = Color(0xFF2E3440),
        foreground = Color(0xFFD8DEE9),
        keyword = Color(0xFF81A1C1),
        string = Color(0xFFA3BE8C),
        number = Color(0xFFB48EAD),
        comment = Color(0xFF616E88),
        function = Color(0xFF88C0D0),
        type = Color(0xFF8FBCBB),
        punctuation = Color(0xFFD8DEE9),
        property = Color(0xFFD8DEE9),
        heading = Color(0xFF88C0D0),
        link = Color(0xFF8FBCBB)
    ),
    GITHUB_LIGHT(
        key = "github_light",
        displayName = "GitHub Light",
        isDark = false,
        background = Color(0xFFFFFFFF),
        foreground = Color(0xFF24292F),
        keyword = Color(0xFFCF222E),
        string = Color(0xFF0A3069),
        number = Color(0xFF0550AE),
        comment = Color(0xFF6E7781),
        function = Color(0xFF8250DF),
        type = Color(0xFF953800),
        punctuation = Color(0xFF24292F),
        property = Color(0xFF0550AE),
        heading = Color(0xFF0550AE),
        link = Color(0xFF0969DA)
    ),
    SOLARIZED_LIGHT(
        key = "solarized_light",
        displayName = "Solarized Light",
        isDark = false,
        background = Color(0xFFFDF6E3),
        foreground = Color(0xFF657B83),
        keyword = Color(0xFF859900),
        string = Color(0xFF2AA198),
        number = Color(0xFFD33682),
        comment = Color(0xFF93A1A1),
        function = Color(0xFF268BD2),
        type = Color(0xFFB58900),
        punctuation = Color(0xFF657B83),
        property = Color(0xFFCB4B16),
        heading = Color(0xFF268BD2),
        link = Color(0xFF268BD2)
    ),
    SOLARIZED_DARK(
        key = "solarized_dark",
        displayName = "Solarized Dark",
        isDark = true,
        background = Color(0xFF002B36),
        foreground = Color(0xFF839496),
        keyword = Color(0xFF859900),
        string = Color(0xFF2AA198),
        number = Color(0xFFD33682),
        comment = Color(0xFF586E75),
        function = Color(0xFF268BD2),
        type = Color(0xFFB58900),
        punctuation = Color(0xFF839496),
        property = Color(0xFFCB4B16),
        heading = Color(0xFF268BD2),
        link = Color(0xFF268BD2)
    );

    companion object {
        fun fromKey(key: String?): CodeTheme =
            entries.firstOrNull { it.key == key } ?: AUTO

        fun resolve(key: String?, dark: Boolean): CodeTheme {
            val chosen = fromKey(key)
            if (chosen != AUTO) return chosen
            return if (dark) DRACULA else GITHUB_LIGHT
        }

        /** Themes offered in the picker, Automatic first. */
        val choices: List<CodeTheme> = entries
    }
}
