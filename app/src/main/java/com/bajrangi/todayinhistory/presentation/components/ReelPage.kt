package com.bajrangi.todayinhistory.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bajrangi.todayinhistory.domain.model.HistoricalEvent
import com.bajrangi.todayinhistory.presentation.theme.YearAmberMuted
import kotlinx.coroutines.delay

/**
 * Full-screen reel page — content-first redesign.
 *
 * Upgrades from game-UI version:
 *   - 32dp horizontal padding (editorial margins)
 *   - Taller gradient veil (60% of screen — more reading comfort)
 *   - Muted year color (supports, doesn't dominate)
 *   - Larger body text (17sp with 30sp line height)
 *   - No swipe hint / arrow chrome — clean, minimal
 *   - Page counter is a whisper (25% alpha)
 *   - Slower, calmer entrance animation (700ms)
 */
@Composable
fun ReelPage(
    event: HistoricalEvent,
    pageIndex: Int,
    totalPages: Int,
    isCurrentPage: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            contentVisible = false
            delay(100)
            contentVisible = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    ) {
        // ── Background: giant year watermark ────────────────
        YearWatermark(year = event.year)

        // ── Bottom gradient veil (taller = more reading area) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.60f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF070B1C).copy(alpha = 0.5f),
                            Color(0xFF070B1C).copy(alpha = 0.85f),
                            Color(0xFF070B1C).copy(alpha = 0.95f),
                            Color(0xFF070B1C),
                        ),
                    ),
                ),
        )

        // ── Content overlay ─────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp) // Editorial margins
                .navigationBarsPadding()
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(600, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        initialOffsetY = { it / 8 }, // Subtle slide
                        animationSpec = tween(700, easing = FastOutSlowInEasing),
                    ),
            ) {
                Column {
                    // Year — muted, not shouty
                    Text(
                        text = "${event.year}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = YearAmberMuted.copy(alpha = 0.85f),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title — large but SemiBold (editorial weight)
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp,
                        ),
                        color = Color.White,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Story — generous line height for reading
                    Text(
                        text = event.aiSummary.ifBlank { event.description },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 30.sp,
                        ),
                        color = Color.White.copy(alpha = 0.75f),
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Page counter — barely visible, no other chrome
                    Text(
                        text = "${pageIndex + 1} of $totalPages",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.25f),
                    )
                }
            }
        }
    }
}
