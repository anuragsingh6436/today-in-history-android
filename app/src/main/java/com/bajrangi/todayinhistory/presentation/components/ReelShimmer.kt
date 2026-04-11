package com.bajrangi.todayinhistory.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Full-screen shimmer skeleton for the reel loading state.
 * Mimics the reel layout with a year bar, title bar, and description bars.
 */
@Composable
fun ReelShimmer(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.04f),
        Color.White.copy(alpha = 0.10f),
        Color.White.copy(alpha = 0.04f),
    )

    val transition = rememberInfiniteTransition(label = "reelShimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "reelShimmerTranslate",
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 300f, 0f),
        end = Offset(translateAnim + 300f, 0f),
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Center: faint year placeholder
        Box(
            modifier = Modifier
                .padding(top = 200.dp)
                .padding(horizontal = 60.dp)
                .fillMaxWidth(0.6f)
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(brush)
                .align(androidx.compose.ui.Alignment.TopCenter),
        )

        // Bottom: content placeholders
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .navigationBarsPadding()
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            // Year badge
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush),
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush),
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Description lines
            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (it == 2) 0.5f else 1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
