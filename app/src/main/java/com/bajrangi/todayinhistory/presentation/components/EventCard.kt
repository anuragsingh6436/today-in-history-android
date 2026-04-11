package com.bajrangi.todayinhistory.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.bajrangi.todayinhistory.presentation.theme.ScrimDark
import com.bajrangi.todayinhistory.presentation.theme.ScrimLight
import com.bajrangi.todayinhistory.presentation.theme.YearAmberMuted

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
fun EventCard(
    event: HistoricalEvent,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFeatured = index == 0
    val context = LocalContext.current
    val yearColor = eraColor(event.year)
    val isDark = isSystemInDarkTheme()
    val scrim = if (isDark) ScrimDark else ScrimLight

    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(onClick = onClick),
    ) {
        Column {
            // ── Image ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(if (isFeatured) 4f / 3f else 16f / 9f),
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

                // Top vignette
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    scrim.copy(alpha = 0.3f),
                                    Color.Transparent,
                                ),
                            ),
                        ),
                )

                // Bottom vignette
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isFeatured) 100.dp else 80.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    scrim.copy(alpha = 0.5f),
                                ),
                            ),
                        ),
                )

                // Year pill
                Text(
                    text = "${event.year}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                    ),
                    color = if (isDark) Color.White else Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                        .background(
                            color = yearColor.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(20.dp),
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                )
            }

            // ── Content ─────────────────────────────────────
            Column(
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 20.dp,
                    bottom = 20.dp,
                ),
            ) {
                Text(
                    text = event.title,
                    style = if (isFeatured) {
                        MaterialTheme.typography.headlineSmall.copy(lineHeight = 30.sp)
                    } else {
                        MaterialTheme.typography.titleLarge.copy(lineHeight = 26.sp)
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = event.aiSummary.ifBlank { event.description },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = if (isFeatured) 4 else 3,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Era tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(yearColor.copy(alpha = 0.5f)),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = eraLabel(event.year),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                    )
                }
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
