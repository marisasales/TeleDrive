package com.drdisagree.teledrive.presentation.preview

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CodeViewerTest {

    @Test
    fun detectsLanguagesByExtension() {
        assertEquals(CodeLanguage.KOTLIN, CodeLanguage.fromFileName("Main.kt"))
        assertEquals(CodeLanguage.PYTHON, CodeLanguage.fromFileName("script.py"))
        assertEquals(CodeLanguage.TYPESCRIPT, CodeLanguage.fromFileName("app.tsx"))
        assertEquals(CodeLanguage.JSON, CodeLanguage.fromFileName("data.json"))
        assertEquals(CodeLanguage.MARKDOWN, CodeLanguage.fromFileName("README.md"))
        assertEquals(CodeLanguage.SHELL, CodeLanguage.fromFileName("Dockerfile"))
        assertEquals(CodeLanguage.PLAIN, CodeLanguage.fromFileName("notes.unknown"))
    }

    @Test
    fun highlightsJsonKeysAndStringValuesDifferently() {
        val text = "{\"name\": \"value\", \"count\": 1}"
        val result = SyntaxHighlighter.highlight(text, CodeLanguage.JSON, CodeTheme.DRACULA)

        val keyStart = text.indexOf("\"name\"")
        val valueStart = text.indexOf("\"value\"")
        val keyStyle = result.spanStyles.first { it.start == keyStart }
        val valueStyle = result.spanStyles.first { it.start == valueStart }

        assertEquals(CodeTheme.DRACULA.property, keyStyle.item.color)
        assertEquals(CodeTheme.DRACULA.string, valueStyle.item.color)
    }

    @Test
    fun coloursProgrammingKeywords() {
        val result = SyntaxHighlighter.highlight(
            "def greet(name):\n    return name\n",
            CodeLanguage.PYTHON,
            CodeTheme.MONOKAI
        )

        assertTrue(result.spanStyles.any { it.item.color == CodeTheme.MONOKAI.keyword })
    }

    @Test
    fun highlightsMarkdownHeadingsAndCode() {
        val result = SyntaxHighlighter.highlight(
            "# Title\n\n```\ncode\n```\n",
            CodeLanguage.MARKDOWN,
            CodeTheme.ONE_DARK
        )

        assertTrue(result.spanStyles.any { it.item.color == CodeTheme.ONE_DARK.heading })
        assertTrue(result.spanStyles.any { it.item.color == CodeTheme.ONE_DARK.string })
    }

    @Test
    fun prettyPrintsNestedJsonWhileKeepingStringsAndNumbersIntact() {
        assertEquals(
            """{
    "items": [
        {
            "name": "a\"b",
            "count": -1.25e+2
        },
        true
    ]
}""",
            prettyPrintJson("{\"items\":[{\"name\":\"a\\\"b\",\"count\":-1.25e+2},true]}")
        )
    }

    @Test
    fun keepsEmptyJsonContainersCompact() {
        assertEquals(
            """{
    "a": {},
    "b": []
}""",
            prettyPrintJson("{\"a\":{},\"b\":[]}")
        )
    }

    @Test
    fun rejectsInvalidJsonSoViewerCanShowOriginalText() {
        assertEquals(null, prettyPrintJson("{\"a\":01}"))
        assertEquals(null, prettyPrintJson("{\"a\":1,}"))
        assertEquals(null, prettyPrintJson("// comment\n{}"))
    }
}
