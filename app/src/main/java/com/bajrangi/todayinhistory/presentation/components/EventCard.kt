package com.bajrangi.todayinhistory.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

/**
 * Event card — always shows an image area.
 *
 * - Has thumbnail → loads from network with Coil
 * - No thumbnail or load error → shows placeholder drawable
 */
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
        val context = LocalContext.current

        GlassSurface(
            modifier = modifier
                .fillMaxWidth()
                .pressScale(pressedScale = 0.98f, onClick = onClick),
        ) {
            Column {
                // Image area — always present
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                ) {
                    if (event.thumbnailUrl.isNotBlank()) {
                        // Load from network
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(event.thumbnailUrl)
                                .crossfade(300)
                                .build(),
                            contentDescription = event.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                PlaceholderImage()
                            },
                            error = {
                                PlaceholderImage()
                            },
                        )
                    } else {
                        // No thumbnail — show placeholder
                        PlaceholderImage()
                    }

                    // Bottom gradient over image for text readability
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

                    // Year badge overlay
                    Text(
                        text = "${event.year}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp)
                            .background(
                                Color.Black.copy(alpha = 0.45f),
                                RoundedCornerShape(8.dp),
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }

                // Text content below image
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
}

/**
 * Placeholder image — used when no thumbnail or when image fails to load.
 */
@Composable
private fun PlaceholderImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.placeholder_history),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxSize(),
    )
}
