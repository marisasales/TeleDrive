package com.drdisagree.teledrive.presentation.preview

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Turns source text into a coloured [AnnotatedString]. Everything is tokenised here instead of a
 * third-party engine: the published highlight libraries are compiled for a newer JVM than this
 * project's toolchain, and a small scanner keeps control over data formats such as JSON and YAML.
 *
 * Results are cached because the viewer recomposes on every zoom step and colouring a large file
 * is the expensive part.
 */
object SyntaxHighlighter {

    private const val CACHE_SIZE = 6

    private val cache = object : LinkedHashMap<String, AnnotatedString>(CACHE_SIZE, 0.75f, true) {
        override fun removeEldestEntry(
            eldest: MutableMap.MutableEntry<String, AnnotatedString>
        ): Boolean = size > CACHE_SIZE
    }

    fun highlight(text: String, language: CodeLanguage, theme: CodeTheme): AnnotatedString {
        if (text.isEmpty() || language == CodeLanguage.PLAIN) return AnnotatedString(text)
        val key = "${language.name}|${theme.key}|${text.length}|${text.hashCode()}"
        synchronized(cache) { cache[key]?.let { return it } }
        val result = runCatching { build(text, language, theme) }
            .getOrElse { AnnotatedString(text) }
        synchronized(cache) { cache[key] = result }
        return result
    }

    private fun build(text: String, language: CodeLanguage, theme: CodeTheme): AnnotatedString {
        val builder = AnnotatedString.Builder(text)
        when (language) {
            CodeLanguage.PLAIN -> Unit
            CodeLanguage.JSON -> json(builder, text, theme)
            CodeLanguage.YAML -> yaml(builder, text, theme)
            CodeLanguage.MARKDOWN -> markdown(builder, text, theme)
            CodeLanguage.XML, CodeLanguage.HTML -> markup(builder, text, theme)
            CodeLanguage.CSS -> css(builder, text, theme)
            CodeLanguage.SQL -> sql(builder, text, theme)
            else -> programming(builder, text, language, theme)
        }
        return builder.toAnnotatedString()
    }

    // ---------------------------------------------------------------- programming

    private fun programming(
        builder: AnnotatedString.Builder,
        text: String,
        language: CodeLanguage,
        theme: CodeTheme
    ) {
        val keywords = keywordsFor(language)
        val hashComments =
            language == CodeLanguage.PYTHON || language == CodeLanguage.SHELL ||
                    language == CodeLanguage.RUBY || language == CodeLanguage.PHP
        val backtickStrings = language == CodeLanguage.JAVASCRIPT ||
                language == CodeLanguage.TYPESCRIPT || language == CodeLanguage.GO ||
                language == CodeLanguage.SHELL
        var i = 0
        var previous = ' '
        while (i < text.length) {
            val c = text[i]
            when {
                c == '\n' -> {
                    previous = c
                    i++
                }

                c.isWhitespace() -> i++

                hashComments && c == '#' -> {
                    val end = text.lineEnd(i)
                    builder.paint(i, end, theme.comment)
                    i = end
                }

                c == '/' && i + 1 < text.length && text[i + 1] == '/' -> {
                    val end = text.lineEnd(i)
                    builder.paint(i, end, theme.comment)
                    i = end
                }

                c == '/' && i + 1 < text.length && text[i + 1] == '*' -> {
                    val close = text.indexOf("*/", i + 2)
                    val end = if (close < 0) text.length else close + 2
                    builder.paint(i, end, theme.comment)
                    i = end
                }

                c == '"' || c == '\'' -> {
                    val triple = text.startsWith(c.toString().repeat(3), i)
                    val end = text.stringEnd(i, c, triple)
                    builder.paint(i, end, theme.string)
                    i = end
                }

                c == '`' && backtickStrings -> {
                    val end = text.stringEnd(i, '`', false)
                    builder.paint(i, end, theme.string)
                    i = end
                }

                c == '@' && i + 1 < text.length && text[i + 1].isLetter() -> {
                    var end = i + 1
                    while (end < text.length && (text[end].isLetterOrDigit() || text[end] == '_')) end++
                    builder.paint(i, end, theme.property)
                    i = end
                }

                c.isDigit() -> {
                    val end = text.numberEnd(i)
                    builder.paint(i, end, theme.number)
                    previous = text[end - 1]
                    i = end
                }

                c.isLetter() || c == '_' -> {
                    var end = i + 1
                    while (end < text.length && (text[end].isLetterOrDigit() || text[end] == '_')) end++
                    val word = text.substring(i, end)
                    when {
                        word.lowercase() in keywords -> builder.paint(i, end, theme.keyword)
                        previous == '.' -> builder.paint(i, end, theme.property)
                        text.isCall(end) -> builder.paint(i, end, theme.function)
                        word.first().isUpperCase() -> builder.paint(i, end, theme.type)
                    }
                    previous = text[end - 1]
                    i = end
                }

                else -> {
                    builder.paint(i, i + 1, theme.punctuation)
                    previous = c
                    i++
                }
            }
        }
    }

    private fun keywordsFor(language: CodeLanguage): Set<String> = when (language) {
        CodeLanguage.KOTLIN -> setOf(
            "package", "import", "class", "object", "interface", "fun", "val", "var", "if",
            "else", "when", "for", "while", "do", "return", "in", "is", "as", "try", "catch",
            "finally", "throw", "null", "true", "false", "this", "super", "override", "private",
            "public", "protected", "internal", "open", "data", "sealed", "abstract", "enum",
            "companion", "lateinit", "init", "constructor", "by", "where", "typealias", "suspend",
            "inline", "operator", "infix", "const", "break", "continue", "until", "downTo", "step",
            "out", "vararg", "reified", "crossinline", "noinline", "expect", "actual", "get", "set"
        )

        CodeLanguage.JAVA -> setOf(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
            "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
            "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native", "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
            "throw", "throws", "transient", "try", "void", "volatile", "while", "var", "record",
            "sealed", "permits", "yield", "true", "false", "null"
        )

        CodeLanguage.PYTHON -> setOf(
            "and", "as", "assert", "async", "await", "break", "class", "continue", "def", "del",
            "elif", "else", "except", "finally", "for", "from", "global", "if", "import", "in",
            "is", "lambda", "nonlocal", "not", "or", "pass", "raise", "return", "try", "while",
            "with", "yield", "True", "False", "None", "self", "match", "case"
        )

        CodeLanguage.JAVASCRIPT, CodeLanguage.TYPESCRIPT -> setOf(
            "async", "await", "break", "case", "catch", "class", "const", "continue", "debugger",
            "default", "delete", "do", "else", "enum", "export", "extends", "false", "finally",
            "for", "from", "function", "if", "implements", "import", "in", "instanceof",
            "interface", "let", "new", "null", "of", "private", "protected", "public", "return",
            "static", "super", "switch", "this", "throw", "true", "try", "type", "typeof",
            "undefined", "var", "void", "while", "with", "yield", "declare", "readonly", "as",
            "namespace", "any", "number", "string", "boolean", "never", "unknown"
        )

        CodeLanguage.C, CodeLanguage.CPP -> setOf(
            "auto", "break", "case", "char", "const", "continue", "default", "do", "double",
            "else", "enum", "extern", "float", "for", "goto", "if", "inline", "int", "long",
            "namespace", "new", "operator", "private", "protected", "public", "register",
            "return", "short", "signed", "sizeof", "static", "struct", "switch", "template",
            "this", "throw", "try", "catch", "typedef", "union", "unsigned", "using", "virtual",
            "void", "volatile", "while", "class", "delete", "explicit", "friend", "mutable",
            "nullptr", "override", "constexpr", "decltype", "noexcept", "true", "false"
        )

        CodeLanguage.CSHARP -> setOf(
            "abstract", "as", "async", "await", "base", "bool", "break", "byte", "case", "catch",
            "char", "checked", "class", "const", "continue", "decimal", "default", "delegate",
            "do", "double", "else", "enum", "event", "explicit", "extern", "false", "finally",
            "fixed", "float", "for", "foreach", "get", "goto", "if", "implicit", "in", "int",
            "interface", "internal", "is", "lock", "long", "namespace", "new", "null", "object",
            "operator", "out", "override", "params", "private", "protected", "public", "readonly",
            "record", "ref", "return", "sealed", "set", "short", "sizeof", "stackalloc", "static",
            "string", "struct", "switch", "this", "throw", "true", "try", "typeof", "uint",
            "ulong", "unchecked", "unsafe", "ushort", "using", "var", "virtual", "void",
            "volatile", "while", "yield", "init", "required"
        )

        CodeLanguage.GO -> setOf(
            "break", "case", "chan", "const", "continue", "default", "defer", "else",
            "fallthrough", "for", "func", "go", "goto", "if", "import", "interface", "map",
            "package", "range", "return", "select", "struct", "switch", "type", "var", "nil",
            "true", "false", "iota", "make", "new", "len", "cap", "append", "delete", "copy"
        )

        CodeLanguage.RUST -> setOf(
            "as", "async", "await", "break", "const", "continue", "crate", "dyn", "else", "enum",
            "extern", "false", "fn", "for", "if", "impl", "in", "let", "loop", "match", "mod",
            "move", "mut", "pub", "ref", "return", "self", "Self", "static", "struct", "super",
            "trait", "true", "type", "unsafe", "use", "where", "while", "Some", "None", "Ok",
            "Err"
        )

        CodeLanguage.SHELL -> setOf(
            "if", "then", "else", "elif", "fi", "for", "while", "until", "do", "done", "case",
            "esac", "in", "function", "return", "exit", "local", "export", "readonly", "declare",
            "set", "unset", "shift", "source", "echo", "printf", "cd", "alias", "eval", "exec",
            "true", "false"
        )

        CodeLanguage.PHP -> setOf(
            "abstract", "and", "array", "as", "break", "callable", "case", "catch", "class",
            "clone", "const", "continue", "declare", "default", "do", "echo", "else", "elseif",
            "empty", "enddeclare", "endfor", "endforeach", "endif", "endswitch", "endwhile",
            "enum", "extends", "final", "finally", "fn", "for", "foreach", "function", "global",
            "goto", "if", "implements", "include", "instanceof", "insteadof", "interface",
            "isset", "list", "match", "namespace", "new", "or", "print", "private", "protected",
            "public", "readonly", "require", "return", "static", "switch", "throw", "trait",
            "try", "unset", "use", "var", "while", "xor", "yield", "true", "false", "null"
        )

        CodeLanguage.RUBY -> setOf(
            "alias", "and", "begin", "break", "case", "class", "def", "defined?", "do", "else",
            "elsif", "end", "ensure", "false", "for", "if", "in", "module", "next", "nil", "not",
            "or", "redo", "rescue", "retry", "return", "self", "super", "then", "true", "undef",
            "unless", "until", "when", "while", "yield", "require", "require_relative", "attr_accessor"
        )

        CodeLanguage.SWIFT -> setOf(
            "associatedtype", "class", "deinit", "enum", "extension", "fileprivate", "func",
            "import", "init", "inout", "internal", "let", "open", "operator", "private",
            "protocol", "public", "rethrows", "static", "struct", "subscript", "typealias", "var",
            "break", "case", "continue", "default", "defer", "do", "else", "fallthrough", "for",
            "guard", "if", "in", "repeat", "return", "switch", "where", "while", "as", "catch",
            "is", "throw", "throws", "try", "nil", "true", "false", "self", "super", "async", "await"
        )

        CodeLanguage.DART -> setOf(
            "abstract", "as", "assert", "async", "await", "break", "case", "catch", "class",
            "const", "continue", "covariant", "default", "deferred", "do", "dynamic", "else",
            "enum", "export", "extends", "extension", "external", "factory", "false", "final",
            "finally", "for", "Function", "get", "hide", "if", "implements", "import", "in",
            "interface", "is", "late", "library", "mixin", "new", "null", "on", "operator",
            "part", "required", "rethrow", "return", "sealed", "set", "show", "static", "super",
            "switch", "sync", "this", "throw", "true", "try", "typedef", "var", "void", "while",
            "with", "yield"
        )

        else -> emptySet()
    }

    // ---------------------------------------------------------------------- JSON

    private fun json(builder: AnnotatedString.Builder, text: String, theme: CodeTheme) {
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                c == '"' -> {
                    val end = text.stringEnd(i, '"', false)
                    var next = end
                    while (next < text.length && text[next].isWhitespace()) next++
                    val isKey = next < text.length && text[next] == ':'
                    builder.paint(i, end, if (isKey) theme.property else theme.string)
                    i = end
                }

                c == '-' || c.isDigit() -> {
                    val end = text.numberEnd(i)
                    builder.paint(i, end, theme.number)
                    i = end
                }

                c.isLetter() -> {
                    var end = i
                    while (end < text.length && text[end].isLetter()) end++
                    val word = text.substring(i, end)
                    if (word == "true" || word == "false" || word == "null") {
                        builder.paint(i, end, theme.keyword)
                    }
                    i = end
                }

                c == '{' || c == '}' || c == '[' || c == ']' || c == ',' || c == ':' -> {
                    builder.paint(i, i + 1, theme.punctuation)
                    i++
                }

                else -> i++
            }
        }
    }

    // ---------------------------------------------------------------------- YAML

    private fun yaml(builder: AnnotatedString.Builder, text: String, theme: CodeTheme) {
        var start = 0
        while (start <= text.length) {
            val lineEnd = text.lineEnd(start)
            var i = start
            while (i < lineEnd && text[i].isWhitespace()) i++
            if (i < lineEnd && text[i] != '#') {
                var keyStart = i
                if (text[keyStart] == '-') {
                    keyStart++
                    while (keyStart < lineEnd && text[keyStart] == ' ') keyStart++
                }
                var keyEnd = keyStart
                while (keyEnd < lineEnd &&
                    (text[keyEnd].isLetterOrDigit() || text[keyEnd] in "._-")
                ) keyEnd++
                var colon = keyEnd
                while (colon < lineEnd && text[colon] == ' ') colon++
                if (keyEnd > keyStart && colon < lineEnd && text[colon] == ':') {
                    builder.paint(keyStart, keyEnd, theme.property)
                }
                var p = keyEnd
                while (p < lineEnd) {
                    val c = text[p]
                    when {
                        c == '#' -> {
                            builder.paint(p, lineEnd, theme.comment)
                            p = lineEnd
                        }

                        c == '"' || c == '\'' -> {
                            val end = text.stringEnd(p, c, false)
                            builder.paint(p, end, theme.string)
                            p = end
                        }

                        c.isDigit() -> {
                            val end = text.numberEnd(p)
                            builder.paint(p, end, theme.number)
                            p = end
                        }

                        c.isLetter() -> {
                            var end = p
                            while (end < lineEnd && text[end].isLetter()) end++
                            if (text.substring(p, end) in YAML_LITERALS) {
                                builder.paint(p, end, theme.keyword)
                            }
                            p = end
                        }

                        else -> p++
                    }
                }
            }
            start = lineEnd + 1
        }
    }

    // ------------------------------------------------------------------ Markdown

    private fun markdown(builder: AnnotatedString.Builder, text: String, theme: CodeTheme) {
        var start = 0
        var inFence = false
        while (start <= text.length) {
            val lineEnd = text.lineEnd(start)
            val line = text.substring(start, lineEnd)
            val trimmed = line.trimStart()
            when {
                trimmed.startsWith("```") || trimmed.startsWith("~~~") -> {
                    builder.paint(start, lineEnd, theme.string)
                    inFence = !inFence
                }

                inFence -> builder.paint(start, lineEnd, theme.string)

                trimmed.startsWith("#") -> builder.paint(start, lineEnd, theme.heading, bold = true)

                trimmed.startsWith(">") -> builder.paint(start, lineEnd, theme.comment)

                else -> inlineMarkdown(builder, text, start, lineEnd, theme)
            }
            start = lineEnd + 1
        }
    }

    private fun inlineMarkdown(
        builder: AnnotatedString.Builder,
        text: String,
        lineStart: Int,
        lineEnd: Int,
        theme: CodeTheme
    ) {
        var marker = lineStart
        while (marker < lineEnd && text[marker] == ' ') marker++
        if (marker + 1 < lineEnd && text[marker] in "-*+" && text[marker + 1] == ' ') {
            builder.paint(marker, marker + 1, theme.keyword)
        }

        var i = lineStart
        while (i < lineEnd) {
            val c = text[i]
            when {
                c == '`' -> {
                    val close = text.indexOf('`', i + 1)
                    val end = if (close < 0 || close >= lineEnd) lineEnd else close + 1
                    builder.paint(i, end, theme.string)
                    i = end
                }

                c == '[' -> {
                    val bracket = text.indexOf(']', i + 1)
                    if (bracket in (i + 1) until lineEnd &&
                        bracket + 1 < lineEnd && text[bracket + 1] == '('
                    ) {
                        val paren = text.indexOf(')', bracket + 2)
                        if (paren in (bracket + 2) until lineEnd) {
                            builder.paint(i, paren + 1, theme.link)
                            i = paren + 1
                            continue
                        }
                    }
                    i++
                }

                c == '*' && i + 1 < lineEnd && text[i + 1] == '*' -> {
                    val close = text.indexOf("**", i + 2)
                    if (close >= i + 2 && close + 2 <= lineEnd) {
                        builder.paint(i, close + 2, theme.foreground, bold = true)
                        i = close + 2
                    } else {
                        i++
                    }
                }

                else -> i++
            }
        }
    }

    // --------------------------------------------------------------- XML / HTML

    private fun markup(builder: AnnotatedString.Builder, text: String, theme: CodeTheme) {
        var i = 0
        while (i < text.length) {
            if (text.startsWith("<!--", i)) {
                val close = text.indexOf("-->", i + 4)
                val end = if (close < 0) text.length else close + 3
                builder.paint(i, end, theme.comment)
                i = end
                continue
            }
            if (text[i] != '<') {
                i++
                continue
            }

            var nameEnd = i + 1
            while (nameEnd < text.length &&
                (text[nameEnd].isLetterOrDigit() || text[nameEnd] in "/?!:.-_")
            ) nameEnd++
            val nameStart = if (i + 1 < text.length && text[i + 1] in "/?!") i + 2 else i + 1
            if (nameStart < nameEnd && text[nameStart].isLetter()) {
                builder.paint(nameStart, nameEnd, theme.keyword)
            }

            var p = nameEnd
            while (p < text.length && text[p] != '>') {
                val c = text[p]
                when {
                    c == '"' || c == '\'' -> {
                        val end = text.stringEnd(p, c, false)
                        builder.paint(p, end, theme.string)
                        p = end
                    }

                    c.isLetter() -> {
                        var end = p
                        while (end < text.length &&
                            (text[end].isLetterOrDigit() || text[end] in ":-_")
                        ) end++
                        var equal = end
                        while (equal < text.length && text[equal] == ' ') equal++
                        if (equal < text.length && text[equal] == '=') {
                            builder.paint(p, end, theme.property)
                        }
                        p = end
                    }

                    else -> p++
                }
            }
            if (p < text.length) {
                builder.paint(p, p + 1, theme.punctuation)
                p++
            }
            i = p
        }
    }

    // ----------------------------------------------------------------------- CSS

    private fun css(builder: AnnotatedString.Builder, text: String, theme: CodeTheme) {
        var i = 0
        while (i < text.length) {
            if (text.startsWith("/*", i)) {
                val close = text.indexOf("*/", i + 2)
                val end = if (close < 0) text.length else close + 2
                builder.paint(i, end, theme.comment)
                i = end
                continue
            }
            val c = text[i]
            when {
                c == '"' || c == '\'' -> {
                    val end = text.stringEnd(i, c, false)
                    builder.paint(i, end, theme.string)
                    i = end
                }

                c == '@' -> {
                    var end = i + 1
                    while (end < text.length && (text[end].isLetter() || text[end] == '-')) end++
                    builder.paint(i, end, theme.keyword)
                    i = end
                }

                c == '.' || c == '#' -> {
                    var end = i + 1
                    while (end < text.length &&
                        (text[end].isLetterOrDigit() || text[end] == '-' || text[end] == '_')
                    ) end++
                    if (end > i + 1) builder.paint(i, end, theme.type)
                    i = end
                }

                c.isDigit() -> {
                    var end = i
                    while (end < text.length &&
                        (text[end].isLetterOrDigit() || text[end] == '.' || text[end] == '%')
                    ) end++
                    builder.paint(i, end, theme.number)
                    i = end
                }

                c.isLetter() -> {
                    var end = i
                    while (end < text.length &&
                        (text[end].isLetterOrDigit() || text[end] == '-' || text[end] == '_')
                    ) end++
                    var colon = end
                    while (colon < text.length && text[colon].isWhitespace()) colon++
                    if (colon < text.length && text[colon] == ':') {
                        builder.paint(i, end, theme.property)
                    }
                    i = end
                }

                c in "{}();,=" -> {
                    builder.paint(i, i + 1, theme.punctuation)
                    i++
                }

                else -> i++
            }
        }
    }

    // ----------------------------------------------------------------------- SQL

    private fun sql(builder: AnnotatedString.Builder, text: String, theme: CodeTheme) {
        var i = 0
        while (i < text.length) {
            if (text.startsWith("--", i)) {
                val end = text.lineEnd(i)
                builder.paint(i, end, theme.comment)
                i = end
                continue
            }
            if (text.startsWith("/*", i)) {
                val close = text.indexOf("*/", i + 2)
                val end = if (close < 0) text.length else close + 2
                builder.paint(i, end, theme.comment)
                i = end
                continue
            }
            val c = text[i]
            when {
                c == '\'' -> {
                    val end = text.stringEnd(i, '\'', false)
                    builder.paint(i, end, theme.string)
                    i = end
                }

                c.isDigit() -> {
                    val end = text.numberEnd(i)
                    builder.paint(i, end, theme.number)
                    i = end
                }

                c.isLetter() || c == '_' -> {
                    var end = i + 1
                    while (end < text.length &&
                        (text[end].isLetterOrDigit() || text[end] == '_')
                    ) end++
                    if (text.substring(i, end).uppercase() in SQL_KEYWORDS) {
                        builder.paint(i, end, theme.keyword)
                    }
                    i = end
                }

                c in "(),;.=*<>+-" -> {
                    builder.paint(i, i + 1, theme.punctuation)
                    i++
                }

                else -> i++
            }
        }
    }

    // ------------------------------------------------------------------- helpers

    private fun AnnotatedString.Builder.paint(
        start: Int,
        end: Int,
        color: Color,
        bold: Boolean = false
    ) {
        if (start >= end) return
        addStyle(
            SpanStyle(color = color, fontWeight = if (bold) FontWeight.Bold else null),
            start,
            end
        )
    }

    private fun String.lineEnd(from: Int): Int {
        val newline = indexOf('\n', from)
        return if (newline < 0) length else newline
    }

    private fun String.isCall(from: Int): Boolean {
        var i = from
        while (i < length && this[i] == ' ') i++
        return i < length && this[i] == '('
    }

    private fun String.numberEnd(from: Int): Int {
        var i = from
        if (i < length && (this[i] == '-' || this[i] == '+')) i++
        if (i + 1 < length && this[i] == '0' && (this[i + 1] == 'x' || this[i + 1] == 'X')) {
            i += 2
            while (i < length && (this[i].isLetterOrDigit())) i++
            return i
        }
        while (i < length && this[i].isDigit()) i++
        if (i < length && this[i] == '.' && i + 1 < length && this[i + 1].isDigit()) {
            i++
            while (i < length && this[i].isDigit()) i++
        }
        if (i < length && (this[i] == 'e' || this[i] == 'E')) {
            var exponent = i + 1
            if (exponent < length && (this[exponent] == '+' || this[exponent] == '-')) exponent++
            if (exponent < length && this[exponent].isDigit()) {
                i = exponent
                while (i < length && this[i].isDigit()) i++
            }
        }
        return i.coerceAtLeast(from + 1)
    }

    private fun String.stringEnd(from: Int, quote: Char, triple: Boolean): Int {
        var i = from + if (triple) 3 else 1
        while (i < length) {
            val c = this[i]
            if (c == '\\') {
                i += 2
                continue
            }
            if (triple) {
                if (c == quote && i + 2 < length && this[i + 1] == quote && this[i + 2] == quote) {
                    return i + 3
                }
            } else {
                if (c == quote) return i + 1
                if (c == '\n') return i
            }
            i++
        }
        return length
    }

    private val YAML_LITERALS = setOf("true", "false", "null", "yes", "no", "on", "off")

    private val SQL_KEYWORDS = setOf(
        "SELECT", "FROM", "WHERE", "INSERT", "INTO", "VALUES", "UPDATE", "SET", "DELETE",
        "CREATE", "ALTER", "DROP", "TABLE", "INDEX", "VIEW", "JOIN", "LEFT", "RIGHT", "INNER",
        "OUTER", "ON", "GROUP", "BY", "ORDER", "HAVING", "LIMIT", "OFFSET", "AS", "AND", "OR",
        "NOT", "NULL", "IS", "IN", "LIKE", "BETWEEN", "DISTINCT", "UNION", "ALL", "CASE", "WHEN",
        "THEN", "ELSE", "END", "PRIMARY", "KEY", "FOREIGN", "REFERENCES", "DEFAULT", "UNIQUE",
        "CONSTRAINT", "EXISTS", "COUNT", "SUM", "AVG", "MIN", "MAX", "BEGIN", "COMMIT",
        "ROLLBACK", "TRANSACTION", "WITH", "RETURNING", "ASC", "DESC", "TRUE", "FALSE"
    )
}
