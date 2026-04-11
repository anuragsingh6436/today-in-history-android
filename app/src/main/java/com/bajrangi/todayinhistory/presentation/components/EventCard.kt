package com.bajrangi.todayinhistory.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bajrangi.todayinhistory.domain.model.HistoricalEvent
import com.bajrangi.todayinhistory.presentation.theme.EraAncientMuted
import com.bajrangi.todayinhistory.presentation.theme.EraCurrentMuted
import com.bajrangi.todayinhistory.presentation.theme.EraModernMuted
import com.bajrangi.todayinhistory.presentation.theme.YearAmberMuted
import kotlinx.coroutines.delay

/** Muted era colors — they support the text, not compete with it. */
private fun eraColor(year: Int) = when {
    year < 1500 -> EraAncientMuted
    year < 1900 -> YearAmberMuted
    year < 2000 -> EraModernMuted
    else        -> EraCurrentMuted
}

/**
 * Content-grade event card.
 *
 * Upgrades from game-UI version:
 *   - 28dp padding (editorial breathing room)
 *   - Calm fade entrance (no bouncy spring — this isn't a game)
 *   - Muted era colors (support hierarchy, don't shout)
 *   - Subtle press scale (0.98f — barely perceptible, premium feel)
 *   - 4-line description (more content visible before tapping)
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
        delay(index * 50L) // Faster stagger — 50ms feels smoother
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500, easing = FastOutSlowInEasing)) +
            slideInVertically(
                initialOffsetY = { it / 6 }, // Shorter slide — subtler
                animationSpec = tween(500, easing = FastOutSlowInEasing),
            ),
    ) {
        GlassSurface(
            modifier = modifier
                .fillMaxWidth()
                .pressScale(pressedScale = 0.98f, onClick = onClick),
        ) {
            Column(modifier = Modifier.padding(28.dp)) {
                // Era-coded year
                val yearColor = eraColor(event.year)
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

                // Title — SemiBold, not Bold (calmer)
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description — 4 lines visible, generous line height
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
}
