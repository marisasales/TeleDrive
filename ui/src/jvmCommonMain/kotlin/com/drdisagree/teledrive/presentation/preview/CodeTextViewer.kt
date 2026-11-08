package com.drdisagree.teledrive.presentation.preview

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.automirrored.filled.WrapText
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drdisagree.teledrive.core.files.MimeTypes
import com.drdisagree.teledrive.presentation.common.MarkdownText
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.preview_text_copy_all
import com.drdisagree.teledrive.resources.preview_text_font_decrease
import com.drdisagree.teledrive.resources.preview_text_font_increase
import com.drdisagree.teledrive.resources.preview_text_line_numbers
import com.drdisagree.teledrive.resources.preview_text_source
import com.drdisagree.teledrive.resources.preview_text_theme
import com.drdisagree.teledrive.resources.preview_text_wrap
import com.drdisagree.teledrive.presentation.components.TipIconButton
import com.drdisagree.teledrive.resources.preview_truncated_download_view
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

private const val TEXT_SCALE_MIN = 0.7f
private const val TEXT_SCALE_MAX = 3f
private const val TEXT_SCALE_STEP = 0.1f
private const val TEXT_SCALE_SAVE_DELAY_MS = 400L

/**
 * Shared read-only text/code viewer.
 */
@Composable
fun CodeTextViewer(
    fileName: String,
    text: String,
    truncated: Boolean,
    storedTextScale: Float,
    onTextScaleChange: (Float) -> Unit,
    showLineNumbers: Boolean,
    onShowLineNumbersChange: (Boolean) -> Unit,
    wrapLines: Boolean,
    onWrapLinesChange: (Boolean) -> Unit,
    themeKey: String,
    onThemeChange: (String) -> Unit,
    onOpenUrl: (String) -> Unit,
    onCopied: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(storedTextScale) }
    LaunchedEffect(storedTextScale) {
        if (abs(storedTextScale - scale) > 0.001f) scale = storedTextScale
    }
    LaunchedEffect(scale) {
        if (abs(scale - storedTextScale) > 0.001f) {
            delay(TEXT_SCALE_SAVE_DELAY_MS)
            onTextScaleChange(scale)
        }
    }

    val isMarkdown = MimeTypes.isMarkdown(MimeTypes.fromFileName(fileName))
    var renderMarkdown by remember(fileName) { mutableStateOf(isMarkdown) }
    var showThemeMenu by remember { mutableStateOf(false) }

    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val theme = CodeTheme.resolve(themeKey, dark)
    val language = CodeLanguage.fromFileName(fileName)
    val clipboard = LocalClipboardManager.current
    val markdownView = isMarkdown && renderMarkdown
    val displayText = remember(fileName, text) {
        if (fileName.substringAfterLast('.', "").equals("json", ignoreCase = true)) {
            prettyPrintJson(text) ?: text
        } else text
    }

    val zoomWithWheel = Modifier.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                if (event.type == PointerEventType.Scroll && event.keyboardModifiers.isCtrlPressed) {
                    val delta = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
                    if (delta != 0f) {
                        scale = (scale - delta * TEXT_SCALE_STEP)
                            .coerceIn(TEXT_SCALE_MIN, TEXT_SCALE_MAX)
                        event.changes.forEach { it.consume() }
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (markdownView) MaterialTheme.colorScheme.background else theme.background
            )
    ) {
        ViewerToolbar(
            theme = theme,
            selectedThemeKey = themeKey,
            scale = scale,
            showLineNumbers = showLineNumbers,
            wrapLines = wrapLines,
            isMarkdown = isMarkdown,
            renderMarkdown = renderMarkdown,
            showThemeMenu = showThemeMenu,
            onShowThemeMenu = { showThemeMenu = it },
            onZoomOut = {
                scale = (scale - TEXT_SCALE_STEP).coerceIn(TEXT_SCALE_MIN, TEXT_SCALE_MAX)
            },
            onZoomIn = {
                scale = (scale + TEXT_SCALE_STEP).coerceIn(TEXT_SCALE_MIN, TEXT_SCALE_MAX)
            },
            onToggleLineNumbers = { onShowLineNumbersChange(!showLineNumbers) },
            onToggleWrap = { onWrapLinesChange(!wrapLines) },
            onToggleSource = { renderMarkdown = !renderMarkdown },
            onPickTheme = {
                showThemeMenu = false
                onThemeChange(it)
            },
            onCopyAll = {
                clipboard.setText(AnnotatedString(displayText))
                onCopied()
            }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .then(zoomWithWheel)
        ) {
            when {
                isMarkdown && renderMarkdown -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    MarkdownText(
                        text = text,
                        textScale = scale,
                        onOpenUrl = onOpenUrl
                    )
                }

                else -> CodeBody(
                    text = displayText,
                    scale = scale,
                    language = language,
                    theme = theme,
                    showLineNumbers = showLineNumbers,
                    wrapLines = wrapLines
                )
            }
        }

        if (truncated) {
            Text(
                text = stringResource(Res.string.preview_truncated_download_view),
                style = MaterialTheme.typography.bodySmall,
                color = if (markdownView) MaterialTheme.colorScheme.onSurfaceVariant
                else theme.comment,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ViewerToolbar(
    theme: CodeTheme,
    selectedThemeKey: String,
    scale: Float,
    showLineNumbers: Boolean,
    wrapLines: Boolean,
    isMarkdown: Boolean,
    renderMarkdown: Boolean,
    showThemeMenu: Boolean,
    onShowThemeMenu: (Boolean) -> Unit,
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    onToggleLineNumbers: () -> Unit,
    onToggleWrap: () -> Unit,
    onToggleSource: () -> Unit,
    onPickTheme: (String) -> Unit,
    onCopyAll: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainer, tonalElevation = 1.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onZoomOut) {
                Icon(
                    Icons.Filled.Remove,
                    contentDescription = stringResource(Res.string.preview_text_font_decrease),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${(scale * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            IconButton(onClick = onZoomIn) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(Res.string.preview_text_font_increase),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ToolbarToggle(
                active = showLineNumbers && !renderMarkdown,
                icon = Icons.Filled.FormatLineSpacing,
                label = stringResource(Res.string.preview_text_line_numbers),
                onClick = onToggleLineNumbers
            )
            ToolbarToggle(
                active = wrapLines,
                icon = Icons.AutoMirrored.Filled.WrapText,
                label = stringResource(Res.string.preview_text_wrap),
                onClick = onToggleWrap
            )
            Box {
                TipIconButton(
                    tooltip = stringResource(Res.string.preview_text_theme),
                    icon = Icons.Filled.Palette,
                    onClick = { onShowThemeMenu(true) }
                )
                DropdownMenu(
                    expanded = showThemeMenu,
                    onDismissRequest = { onShowThemeMenu(false) }
                ) {
                    CodeTheme.choices.forEach { choice ->
                        DropdownMenuItem(
                            text = { Text(choice.displayName) },
                            leadingIcon = {
                                if (choice.key == CodeTheme.fromKey(selectedThemeKey).key) {
                                    Icon(Icons.Filled.Check, contentDescription = null)
                                }
                            },
                            onClick = { onPickTheme(choice.key) }
                        )
                    }
                }
            }
            TipIconButton(
                tooltip = stringResource(Res.string.preview_text_copy_all),
                icon = Icons.Filled.ContentCopy,
                onClick = onCopyAll
            )
            if (isMarkdown) {
                ToolbarToggle(
                    active = !renderMarkdown,
                    icon = Icons.Filled.Code,
                    label = stringResource(Res.string.preview_text_source),
                    onClick = onToggleSource
                )
            }
        }
    }
}

@Composable
private fun ToolbarToggle(
    active: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    TipIconButton(
        tooltip = label,
        icon = icon,
        onClick = onClick,
        tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun CodeBody(
    text: String,
    scale: Float,
    language: CodeLanguage,
    theme: CodeTheme,
    showLineNumbers: Boolean,
    wrapLines: Boolean
) {
    val style = codeTextStyle(scale, theme)
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val highlighted by produceState<AnnotatedString?>(
        initialValue = null,
        text,
        language,
        theme.key,
        theme.isDark
    ) {
        value = withContext(Dispatchers.Default) {
            SyntaxHighlighter.highlight(text, language, theme)
        }
    }
    val rendered = highlighted ?: AnnotatedString(text)
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(verticalScroll)
            .padding(vertical = 8.dp)
    ) {
        if (showLineNumbers) {
            LineNumberGutter(
                text = text,
                layout = layout,
                style = style,
                scale = scale,
                theme = theme
            )
        }
        SelectionContainer(
            modifier = if (wrapLines) Modifier.weight(1f) else Modifier
        ) {
            if (wrapLines) {
                Text(
                    text = rendered,
                    style = style,
                    softWrap = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    onTextLayout = { layout = it }
                )
            } else {
                Box(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                    Text(
                        text = rendered,
                        style = style,
                        softWrap = false,
                        overflow = TextOverflow.Visible,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        onTextLayout = { layout = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun LineNumberGutter(
    text: String,
    layout: TextLayoutResult?,
    style: TextStyle,
    scale: Float,
    theme: CodeTheme
) {
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val numberStyle = style.copy(
        fontSize = (11f * scale).sp,
        lineHeight = style.lineHeight,
        color = theme.comment
    )
    val lineStarts = remember(text) {
        buildList {
            add(0)
            text.forEachIndexed { index, char ->
                if (char == '\n' && index + 1 < text.length) add(index + 1)
            }
        }
    }
    val digits = lineStarts.size.toString().length
    val gutterWidth = with(density) {
        measurer.measure(AnnotatedString("9".repeat(digits)), numberStyle).size.width.toDp()
    } + 16.dp
    val gutterHeight = layout?.let { with(density) { it.size.height.toDp() } } ?: 0.dp

    Canvas(
        modifier = Modifier
            .width(gutterWidth)
            .height(gutterHeight)
            .padding(horizontal = 4.dp)
    ) {
        val result = layout ?: return@Canvas
        val rightPad = 4.dp.toPx()
        lineStarts.forEachIndexed { index, offset ->
            if (offset !in text.indices) return@forEachIndexed
            val visualLine = result.getLineForOffset(offset)
            val measured = measurer.measure(AnnotatedString((index + 1).toString()), numberStyle)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(
                    x = size.width - rightPad - measured.size.width,
                    y = result.getLineTop(visualLine).toFloat()
                )
            )
        }
    }
}

private fun codeTextStyle(scale: Float, theme: CodeTheme): TextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = (13f * scale).sp,
    lineHeight = (19f * scale).sp,
    color = theme.foreground
)
