package com.drdisagree.teledrive.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import com.drdisagree.teledrive.presentation.platform.LocalPlatformCapabilities
import com.drdisagree.teledrive.resources.Res
import com.drdisagree.teledrive.resources.common_retry

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    if (LocalPlatformCapabilities.current.isDesktop) {
        val shimmer by rememberInfiniteTransition(label = "desktopLoading").animateFloat(
            initialValue = 0.35f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
            label = "desktopLoadingAlpha"
        )
        Column(
            modifier = modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(6) { index ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SkeletonBlock(48.dp, 48.dp, shimmer)
                    Spacer(Modifier.size(14.dp))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SkeletonBlock(if (index % 2 == 0) 180.dp else 136.dp, 11.dp, shimmer)
                        SkeletonBlock(96.dp, 8.dp, shimmer * 0.75f)
                    }
                }
            }
        }
        return
    }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LoadingIndicator(modifier = Modifier.size(56.dp))
    }
}

@Composable
private fun SkeletonBlock(width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp, alpha: Float) {
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = alpha))
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        description?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            Button(onClick = onAction, shapes = ButtonDefaults.shapes()) {
                Text(actionLabel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        onRetry?.let {
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = it,
                shapes = ButtonDefaults.shapes()
            ) { Text(stringResource(Res.string.common_retry)) }
        }
    }
}
