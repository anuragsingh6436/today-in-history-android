package com.bajrangi.todayinhistory.presentation.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bajrangi.todayinhistory.R
import com.bajrangi.todayinhistory.domain.model.HistoricalEvent
import com.bajrangi.todayinhistory.presentation.components.AppBackground
import com.bajrangi.todayinhistory.presentation.components.GlassSurface
import com.bajrangi.todayinhistory.presentation.components.GlassSurfaceAccent
import com.bajrangi.todayinhistory.presentation.theme.EraAncientMuted
import com.bajrangi.todayinhistory.presentation.theme.EraCurrentMuted
import com.bajrangi.todayinhistory.presentation.theme.EraModernMuted
import com.bajrangi.todayinhistory.presentation.theme.IceBlue
import com.bajrangi.todayinhistory.presentation.theme.PaperFaint
import com.bajrangi.todayinhistory.presentation.theme.ScrimDark
import com.bajrangi.todayinhistory.presentation.theme.ScrimLight
import com.bajrangi.todayinhistory.presentation.theme.YearAmberMuted
import com.bajrangi.todayinhistory.presentation.components.pressScale
import kotlinx.coroutines.delay
import java.time.Month
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

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

    var heroVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        heroVisible = true
        delay(200)
        titleVisible = true
        delay(200)
        contentVisible = true
    }

    val context = LocalContext.current
    val yearColor = eraColor(event.year)
    val isDark = isSystemInDarkTheme()
    val scrim = if (isDark) ScrimDark else ScrimLight

    AppBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Hero Image Section ──────────────────────────
            AnimatedVisibility(
                visible = heroVisible,
                enter = fadeIn(tween(700, easing = FastOutSlowInEasing)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f),
                ) {
                    // Image
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
                        Image(
                            painter = painterResource(R.drawable.placeholder_history),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    // Top gradient (for back button readability)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        scrim.copy(alpha = 0.6f),
                                        Color.Transparent,
                                    ),
                                ),
                            ),
                    )

                    // Bottom gradient (blends into content)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        scrim.copy(alpha = 0.7f),
                                        scrim,
                                    ),
                                ),
                            ),
                    )

                    // Back button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(start = 8.dp, top = 8.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = scrim.copy(alpha = 0.4f),
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    // Era + Year badge on image (bottom-left)
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 28.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(yearColor),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = eraLabel(event.year),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${event.year}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .background(
                                    yearColor.copy(alpha = 0.6f),
                                    RoundedCornerShape(16.dp),
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            // ── Title Section ───────────────────────────────
            AnimatedVisibility(
                visible = titleVisible,
                enter = fadeIn(tween(500, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(500, easing = FastOutSlowInEasing),
                    ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp)
                        .padding(top = 20.dp),
                ) {
                    // Title — the hero of the page
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 36.sp,
                            letterSpacing = (-0.3).sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Date eyebrow
                    val monthName = if (event.month in 1..12) {
                        Month.of(event.month)
                            .getDisplayName(JavaTextStyle.FULL, Locale.getDefault())
                    } else ""

                    if (monthName.isNotEmpty()) {
                        Text(
                            text = "$monthName ${event.day}, ${event.year}".uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.2.sp,
                            ),
                            color = PaperFaint.copy(alpha = 0.5f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Content Card ────────────────────────────────
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(500, easing = FastOutSlowInEasing)) +
                    slideInVertically(
                        initialOffsetY = { it / 6 },
                        animationSpec = tween(600, easing = FastOutSlowInEasing),
                    ),
            ) {
                GlassSurfaceAccent(
                    accentColor = yearColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                ) {
                    Column(modifier = Modifier.padding(28.dp)) {
                        // What happened section
                        Text(
                            text = "WHAT HAPPENED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.2.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 30.sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        )

                        // AI Summary section
                        if (event.aiSummary.isNotBlank()) {
                            Spacer(modifier = Modifier.height(28.dp))

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Text(
                                text = "AI INSIGHT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.2.sp,
                                ),
                                color = IceBlue.copy(alpha = 0.4f),
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = event.aiSummary,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 30.sp,
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            )
                        }

                        // Wikipedia CTA
                        if (event.wikipediaUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(32.dp))

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Button(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(event.wikipediaUrl),
                                    )
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = IceBlue.copy(alpha = 0.12f),
                                    contentColor = IceBlue,
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Read full article on Wikipedia",
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
