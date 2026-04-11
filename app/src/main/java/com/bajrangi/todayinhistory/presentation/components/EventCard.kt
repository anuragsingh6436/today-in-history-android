package com.bajrangi.todayinhistory.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
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

@Composable
fun EventCard(
    event: HistoricalEvent,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 50L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500, easing = FastOutSlowInEasing)) +
            slideInVertically(
                initialOffsetY = { it / 6 },
                animationSpec = tween(500, easing = FastOutSlowInEasing),
            ),
    ) {
        if (event.thumbnailUrl.isNotBlank()) {
            ImageEventCard(event = event, onClick = onClick, modifier = modifier)
        } else {
            TextOnlyEventCard(event = event, onClick = onClick, modifier = modifier)
        }
    }
}

@Composable
private fun ImageEventCard(
    event: HistoricalEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(pressedScale = 0.98f, onClick = onClick),
    ) {
        Column {
            // Hero image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(event.thumbnailUrl)
                        .crossfade(300)
                        .build(),
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        // Shimmer placeholder while loading
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                        )
                    },
                    error = {
                        // Fallback on error — show colored background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            eraColor(event.year).copy(alpha = 0.15f),
                                            MaterialTheme.colorScheme.surfaceVariant,
                                        ),
                                    ),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "${event.year}",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = eraColor(event.year).copy(alpha = 0.3f),
                            )
                        }
                    },
                )

                // Bottom gradient over image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF070B1C).copy(alpha = 0.4f),
                                ),
                            ),
                        ),
                )

                // Year badge on image
                Text(
                    text = "${event.year}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(
                            Color.Black.copy(alpha = 0.4f),
                            RoundedCornerShape(8.dp),
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }

            // Text content
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = event.aiSummary.ifBlank { event.description },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun TextOnlyEventCard(
    event: HistoricalEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val yearColor = eraColor(event.year)

    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(pressedScale = 0.98f, onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(
                    modifier = Modifier
                        .width(4.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(yearColor.copy(alpha = 0.7f)),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${event.year}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = yearColor,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = event.aiSummary.ifBlank { event.description },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
