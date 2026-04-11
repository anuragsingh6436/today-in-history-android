package com.bajrangi.todayinhistory.presentation.components

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

private fun eraColor(year: Int) = when {
    year < 1500 -> EraAncientMuted
    year < 1900 -> YearAmberMuted
    year < 2000 -> EraModernMuted
    else        -> EraCurrentMuted
}

/**
 * Event card — clean, scroll-safe, no entrance animation.
 *
 * Always shows an image area:
 *   - Has thumbnail → Coil loads from network
 *   - No thumbnail or error → placeholder drawable
 */
@Composable
fun EventCard(
    event: HistoricalEvent,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(onClick = onClick),
    ) {
        Column {
            // ── Image area ──────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            ) {
                if (event.thumbnailUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(event.thumbnailUrl)
                            .crossfade(300)
                            .build(),
                        contentDescription = event.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = { PlaceholderImage() },
                        error = { PlaceholderImage() },
                    )
                } else {
                    PlaceholderImage()
                }

                // Gradient over bottom of image
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

                // Year badge
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

            // ── Text content ────────────────────────────────
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
private fun PlaceholderImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.placeholder_history),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxSize(),
    )
}
