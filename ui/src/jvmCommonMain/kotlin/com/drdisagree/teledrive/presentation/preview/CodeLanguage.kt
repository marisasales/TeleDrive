package com.drdisagree.teledrive.presentation.preview

/**
 * Languages the viewer can colour. Programming languages share the generic lexer in
 * [SyntaxHighlighter]; data and markup formats have their own small tokenizers.
 */
enum class CodeLanguage(val displayName: String) {
    KOTLIN("Kotlin"),
    JAVA("Java"),
    PYTHON("Python"),
    JAVASCRIPT("JavaScript"),
    TYPESCRIPT("TypeScript"),
    C("C"),
    CPP("C++"),
    CSHARP("C#"),
    GO("Go"),
    RUST("Rust"),
    SHELL("Shell"),
    PHP("PHP"),
    RUBY("Ruby"),
    SWIFT("Swift"),
    DART("Dart"),
    SQL("SQL"),
    JSON("JSON"),
    YAML("YAML"),
    MARKDOWN("Markdown"),
    XML("XML"),
    HTML("HTML"),
    CSS("CSS"),
    PLAIN("Text");

    companion object {
        fun fromFileName(fileName: String): CodeLanguage {
            val lower = fileName.lowercase()
            val extension = lower.substringAfterLast('.', "")
            if (extension.isEmpty()) {
                return if (lower == "dockerfile" || lower == "makefile") SHELL else PLAIN
            }
            return when (extension) {
                "kt", "kts" -> KOTLIN
                "java" -> JAVA
                "py", "pyw" -> PYTHON
                "js", "mjs", "cjs", "jsx" -> JAVASCRIPT
                "ts", "tsx" -> TYPESCRIPT
                "c", "h" -> C
                "cpp", "cc", "cxx", "hpp", "hxx", "m", "mm" -> CPP
                "cs" -> CSHARP
                "go" -> GO
                "rs" -> RUST
                "sh", "bash", "zsh", "fish", "ps1", "bat", "cmd" -> SHELL
                "php" -> PHP
                "rb" -> RUBY
                "swift" -> SWIFT
                "dart" -> DART
                "sql" -> SQL
                "json", "jsonc", "json5" -> JSON
                "yml", "yaml" -> YAML
                "md", "markdown", "rst" -> MARKDOWN
                "xml", "svg", "xhtml" -> XML
                "html", "htm" -> HTML
                "css", "scss", "sass", "less" -> CSS
                else -> PLAIN
            }
        }
    }
}
