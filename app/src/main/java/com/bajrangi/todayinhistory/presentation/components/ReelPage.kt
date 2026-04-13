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
import androidx.compose.foundation.isSystemInDarkTheme
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bajrangi.todayinhistory.R
import com.bajrangi.todayinhistory.domain.model.HistoricalEvent
import com.bajrangi.todayinhistory.presentation.theme.EraAncientMuted
import com.bajrangi.todayinhistory.presentation.theme.EraCurrentMuted
import com.bajrangi.todayinhistory.presentation.theme.EraModernMuted
import com.bajrangi.todayinhistory.presentation.theme.ScrimDark
import com.bajrangi.todayinhistory.presentation.theme.ScrimLight
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
    val isDark = isSystemInDarkTheme()
    val scrim = if (isDark) ScrimDark else ScrimLight
    val textPrimary = if (isDark) Color.White else Color(0xFF1B1F36)
    val textSecondary = if (isDark) Color.White.copy(alpha = 0.7f) else Color(0xFF1B1F36).copy(alpha = 0.7f)
    val textTertiary = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF1B1F36).copy(alpha = 0.5f)

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
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(event.thumbnailUrl)
                    .crossfade(200)
                    .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                    .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                    .build(),
                contentDescription = event.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(R.drawable.placeholder_history),
                error = painterResource(R.drawable.placeholder_history),
            )
        } else {
            PlaceholderFull()
        }

        // ── Top vignette ────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            scrim.copy(alpha = 0.5f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        // ── Bottom vignette ─────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.65f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            scrim.copy(alpha = 0.4f),
                            scrim.copy(alpha = 0.8f),
                            scrim.copy(alpha = 0.95f),
                            scrim,
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
                            color = textTertiary,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${event.year}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                            ),
                            color = textPrimary,
                            modifier = Modifier
                                .background(
                                    yearColor.copy(alpha = 0.6f),
                                    RoundedCornerShape(16.dp),
                                )
                                .padding(horizontal = 12.dp, vertical = 3.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 34.sp,
                            letterSpacing = (-0.3).sp,
                        ),
                        color = textPrimary,
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
                        color = textSecondary,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Page counter
                    Text(
                        text = "${pageIndex + 1} of $totalPages",
                        style = MaterialTheme.typography.labelSmall,
                        color = textTertiary.copy(alpha = 0.4f),
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderFull(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.placeholder_history),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxSize(),
    )
}
