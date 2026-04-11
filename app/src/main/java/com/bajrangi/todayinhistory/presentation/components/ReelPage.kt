package com.bajrangi.todayinhistory.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.bajrangi.todayinhistory.R
import com.bajrangi.todayinhistory.domain.model.HistoricalEvent
import com.bajrangi.todayinhistory.presentation.theme.EraAncientMuted
import com.bajrangi.todayinhistory.presentation.theme.EraCurrentMuted
import com.bajrangi.todayinhistory.presentation.theme.EraModernMuted
import com.bajrangi.todayinhistory.presentation.theme.YearAmberMuted
import kotlinx.coroutines.delay

private fun eraColor(year: Int) = when {
    year < 1500 -> EraAncientMuted
    year < 1900 -> YearAmberMuted
    year < 2000 -> EraModernMuted
    else        -> EraCurrentMuted
}

private fun eraLabel(year: Int) = when {
    year < 1500 -> "Ancient"
    year < 1900 -> "Pre-Modern"
    year < 2000 -> "Modern Era"
    else        -> "Contemporary"
}

/**
 * Full-screen reel page — immersive, image-driven.
 *
 * Structure:
 *   - Hero image fills entire screen (or placeholder)
 *   - Dual vignette (top + bottom) for depth
 *   - Content floats at the bottom over gradient
 *   - Era tag + year pill on image
 *   - Title as hero, description below
 *   - Page counter as whisper
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
    val context = LocalContext.current
    val yearColor = eraColor(event.year)

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
        // ── Full-screen image ───────────────────────────────
        if (event.thumbnailUrl.isNotBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(event.thumbnailUrl)
                    .crossfade(400)
                    .build(),
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Image(
                        painter = painterResource(R.drawable.placeholder_history),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                },
                error = {
                    Image(
                        painter = painterResource(R.drawable.placeholder_history),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                },
            )
        } else {
            Image(
                painter = painterResource(R.drawable.placeholder_history),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // ── Top vignette (subtle depth from above) ──────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF070B1C).copy(alpha = 0.5f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        // ── Bottom vignette (reading zone) ──────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.65f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF070B1C).copy(alpha = 0.4f),
                            Color(0xFF070B1C).copy(alpha = 0.8f),
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
                .padding(horizontal = 32.dp)
                .navigationBarsPadding()
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(600, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        initialOffsetY = { it / 8 },
                        animationSpec = tween(700, easing = FastOutSlowInEasing),
                    ),
            ) {
                Column {
                    // Era tag + year pill
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(yearColor),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = eraLabel(event.year),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${event.year}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                            ),
                            color = Color.White,
                            modifier = Modifier
                                .background(
                                    yearColor.copy(alpha = 0.6f),
                                    RoundedCornerShape(16.dp),
                                )
                                .padding(horizontal = 12.dp, vertical = 3.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title — hero text
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 34.sp,
                            letterSpacing = (-0.3).sp,
                        ),
                        color = Color.White,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description
                    Text(
                        text = event.aiSummary.ifBlank { event.description },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 28.sp,
                        ),
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Page counter
                    Text(
                        text = "${pageIndex + 1} of $totalPages",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.2f),
                    )
                }
            }
        }
    }
}
