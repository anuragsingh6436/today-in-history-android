package com.bajrangi.todayinhistory.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.bajrangi.todayinhistory.presentation.theme.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * Aurora Background — the shared Bajrangi app backdrop.
 *
 * Extracted from XOMaster & SudokuPro. Features:
 * - Radial vignette gradient (core → mid → edge)
 * - Slow-drifting ice-blue orb (20s cycle, sine/cosine drift)
 * - Vignette center at y = 42% (draws the eye upward)
 *
 * Parameters are pixel-identical to the other apps.
 */
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val isDark = isSystemInDarkTheme()

    // Orb drift: 20-second infinite cycle (matching XOMaster/SudokuPro)
    val transition = rememberInfiniteTransition(label = "orbDrift")
    val driftPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2832f, // 2π
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "driftPhase",
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Colors per theme
            val core = if (isDark) VignetteCoreDark else VignetteCoreLight
            val mid = if (isDark) VignetteMidDark else VignetteMidLight
            val edge = if (isDark) VignetteEdgeDark else VignetteEdgeLight
            val orbAlpha = if (isDark) OrbAlphaDark else OrbAlphaLight

            // 1. Radial vignette — center at (50%, 42%)
            val vCenter = Offset(w * 0.5f, h * 0.42f)
            val vRadius = max(w, h) * 0.95f

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(core, mid, edge),
                    center = vCenter,
                    radius = vRadius,
                ),
            )

            // 2. Drifting ice-blue orb
            val orbX = w * 0.78f + sin(driftPhase) * w * 0.05f
            val orbY = h * 0.18f + cos(driftPhase * 0.7f) * h * 0.03f
            val orbRadius = w * 0.55f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuroraOrb.copy(alpha = orbAlpha),
                        Color.Transparent,
                    ),
                    center = Offset(orbX, orbY),
                    radius = orbRadius,
                ),
                radius = orbRadius,
                center = Offset(orbX, orbY),
            )
        }

        content()
    }
}
