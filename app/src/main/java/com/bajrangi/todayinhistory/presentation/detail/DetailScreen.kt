package com.bajrangi.todayinhistory.presentation.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bajrangi.todayinhistory.domain.model.HistoricalEvent
import com.bajrangi.todayinhistory.presentation.components.AppBackground
import com.bajrangi.todayinhistory.presentation.components.GlassSurfaceAccent
import com.bajrangi.todayinhistory.presentation.theme.IceBlue
import com.bajrangi.todayinhistory.presentation.theme.PaperFaint
import com.bajrangi.todayinhistory.presentation.theme.YearAmberMuted
import kotlinx.coroutines.delay
import java.time.Month
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

/**
 * Detail screen — editorial reading experience.
 *
 * Content-first upgrades:
 *   - 32dp horizontal margins (matches editorial standards)
 *   - 80sp year hero (architectural, not decorative)
 *   - Section labels at 30% alpha (utility, not chrome)
 *   - 17sp body text with 30sp line height (reading-optimized)
 *   - Calmer entrance animation (700ms, FastOutSlowIn)
 *   - Accent glass tinted with muted amber (barely perceptible)
 */
@Composable
fun DetailScreen(
    event: HistoricalEvent?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (event == null) {
        AppBackground {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Event not found", color = MaterialTheme.colorScheme.onSurface)
            }
        }
        return
    }

    var headerVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        headerVisible = true
        delay(250)
        contentVisible = true
    }

    val context = LocalContext.current

    AppBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Back button ─────────────────────────────────
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(start = 12.dp, top = 12.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }

            // ── Hero header ─────────────────────────────────
            AnimatedVisibility(
                visible = headerVisible,
                enter = fadeIn(tween(600, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        initialOffsetY = { -it / 5 },
                        animationSpec = tween(600, easing = FastOutSlowInEasing),
                    ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .padding(top = 4.dp),
                ) {
                    // Eyebrow: date
                    val monthName = if (event.month in 1..12) {
                        Month.of(event.month)
                            .getDisplayName(JavaTextStyle.FULL, Locale.getDefault())
                    } else ""

                    if (monthName.isNotEmpty()) {
                        Text(
                            text = "$monthName ${event.day}".uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = PaperFaint,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    // Year — architectural hero
                    Text(
                        text = "${event.year}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 80.sp,
                            letterSpacing = (-4).sp,
                        ),
                        color = YearAmberMuted,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Content card ────────────────────────────────
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(500, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        initialOffsetY = { it / 6 },
                        animationSpec = tween(600, easing = FastOutSlowInEasing),
                    ),
            ) {
                GlassSurfaceAccent(
                    accentColor = YearAmberMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                ) {
                    Column(modifier = Modifier.padding(32.dp)) {
                        // Title
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                lineHeight = 36.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        )

                        // AI Summary section
                        if (event.aiSummary.isNotBlank()) {
                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "AI SUMMARY",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = event.aiSummary,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 30.sp,
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            )
                        }

                        // Historical record section
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "WHAT HAPPENED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 30.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        )

                        // Wikipedia link
                        if (event.wikipediaUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(32.dp))

                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(event.wikipediaUrl),
                                    )
                                    context.startActivity(intent)
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = IceBlue.copy(alpha = 0.7f),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Read on Wikipedia",
                                    color = IceBlue.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}
