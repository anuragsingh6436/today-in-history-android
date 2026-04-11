package com.bajrangi.todayinhistory.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bajrangi.todayinhistory.presentation.theme.LocalGlassColors

/**
 * Content-grade GlassSurface.
 *
 * Evolved from the game-UI version — same 3-layer structure but
 * dialled back for a reading experience:
 *   - Softer shadow (less depth, calmer)
 *   - Thinner border (0.5dp vs 1dp — less visible chrome)
 *   - Larger corner radius (24dp — feels more editorial)
 *   - Lower default elevation (4dp — cards float gently)
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val glass = LocalGlassColors.current

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = if (isDark) 0.25f else 0.06f),
                spotColor = Color.Black.copy(alpha = if (isDark) 0.15f else 0.04f),
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(glass.fill, glass.fill.copy(alpha = glass.fill.alpha * 0.9f)),
                ),
            )
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glass.border.copy(alpha = glass.border.alpha * 0.6f),
                        glass.border.copy(alpha = glass.border.alpha * 0.15f),
                    ),
                ),
                shape = shape,
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(glass.inner, Color.Transparent),
                    startY = 0f,
                    endY = 100f,
                ),
            ),
        content = content,
    )
}

/**
 * Accent-tinted GlassSurface for the detail screen.
 *
 * Content-grade version: accent tint is barely perceptible,
 * border is a whisper — the text does the talking.
 */
@Composable
fun GlassSurfaceAccent(
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: Dp = 4.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val glass = LocalGlassColors.current

    val tint = accentColor.copy(alpha = if (isDark) 0.08f else 0.04f)
    val borderTop = accentColor.copy(alpha = if (isDark) 0.25f else 0.15f)
    val borderBot = accentColor.copy(alpha = if (isDark) 0.08f else 0.04f)

    Column(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = accentColor.copy(alpha = if (isDark) 0.15f else 0.06f),
                spotColor = accentColor.copy(alpha = if (isDark) 0.10f else 0.04f),
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(glass.fill, glass.fill.copy(alpha = glass.fill.alpha * 0.9f)),
                ),
            )
            .background(tint)
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(colors = listOf(borderTop, borderBot)),
                shape = shape,
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(glass.inner, Color.Transparent),
                    startY = 0f,
                    endY = 100f,
                ),
            ),
        content = content,
    )
}
