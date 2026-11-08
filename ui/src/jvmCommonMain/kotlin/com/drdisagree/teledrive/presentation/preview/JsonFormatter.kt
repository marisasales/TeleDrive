package com.drdisagree.teledrive.presentation.preview

/** Strict JSON pretty-printer. Invalid input is left untouched by the caller. */
internal fun prettyPrintJson(source: String): String? = runCatching {
    JsonPrettyPrinter(source).format()
}.getOrNull()

private class JsonPrettyPrinter(private val source: String) {
    private var index = 0

    fun format(): String {
        whitespace()
        val result = value(0)
        whitespace()
        require(index == source.length)
        return result
    }

    private fun value(depth: Int): String {
        whitespace()
        return when (peek()) {
            '{' -> objectValue(depth)
            '[' -> arrayValue(depth)
            '"' -> stringValue()
            't' -> literal("true")
            'f' -> literal("false")
            'n' -> literal("null")
            '-', in '0'..'9' -> numberValue()
            else -> error("Invalid JSON")
        }
    }

    private fun objectValue(depth: Int): String {
        index++
        whitespace()
        if (consume('}')) return "{}"
        val fields = mutableListOf<String>()
        while (true) {
            whitespace()
            require(peek() == '"')
            val key = stringValue()
            whitespace()
            require(consume(':'))
            val child = value(depth + 1)
            fields += "${indent(depth + 1)}$key: $child"
            whitespace()
            if (consume('}')) break
            require(consume(','))
        }
        return "{\n${fields.joinToString(",\n")}\n${indent(depth)}}"
    }

    private fun arrayValue(depth: Int): String {
        index++
        whitespace()
        if (consume(']')) return "[]"
        val items = mutableListOf<String>()
        while (true) {
            items += indent(depth + 1) + value(depth + 1)
            whitespace()
            if (consume(']')) break
            require(consume(','))
        }
        return "[\n${items.joinToString(",\n")}\n${indent(depth)}]"
    }

    private fun stringValue(): String {
        val start = index
        require(consume('"'))
        while (index < source.length) {
            when (val char = source[index++]) {
                '"' -> return source.substring(start, index)
                '\\' -> {
                    require(index < source.length)
                    val escape = source[index++]
                    if (escape == 'u') {
                        repeat(4) { require(index < source.length && source[index++].isHexDigit()) }
                    } else require(escape in "\\\"/bfnrt")
                }
                else -> require(char >= ' ')
            }
        }
        error("Unterminated string")
    }

    private fun numberValue(): String {
        val start = index
        consume('-')
        if (!consume('0')) {
            require(peek() in '1'..'9')
            while (peek() in '0'..'9') index++
        }
        if (consume('.')) {
            require(peek() in '0'..'9')
            while (peek() in '0'..'9') index++
        }
        if (peek() == 'e' || peek() == 'E') {
            index++
            if (peek() == '+' || peek() == '-') index++
            require(peek() in '0'..'9')
            while (peek() in '0'..'9') index++
        }
        return source.substring(start, index)
    }

    private fun literal(expected: String): String {
        require(source.regionMatches(index, expected, 0, expected.length))
        index += expected.length
        return expected
    }

    private fun indent(depth: Int) = "    ".repeat(depth)
    private fun whitespace() { while (peek() == ' ' || peek() == '\n' || peek() == '\r' || peek() == '\t') index++ }
    private fun peek(): Char = source.getOrElse(index) { '\u0000' }
    private fun consume(char: Char): Boolean = if (peek() == char) { index++; true } else false
    private fun Char.isHexDigit(): Boolean = this in '0'..'9' || this in 'a'..'f' || this in 'A'..'F'
}
