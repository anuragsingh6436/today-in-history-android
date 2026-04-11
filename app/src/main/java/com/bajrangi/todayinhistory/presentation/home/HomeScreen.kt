package com.bajrangi.todayinhistory.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bajrangi.todayinhistory.presentation.components.AppBackground
import com.bajrangi.todayinhistory.presentation.components.ReelPage
import com.bajrangi.todayinhistory.presentation.components.ReelShimmer
import com.bajrangi.todayinhistory.presentation.theme.IceBlue
import com.bajrangi.todayinhistory.presentation.theme.PaperFaint
import com.bajrangi.todayinhistory.presentation.theme.RoseGold
import kotlinx.coroutines.delay
import java.time.Month
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

/**
 * Home screen — full-screen History Reels.
 *
 * Content-first upgrades:
 *   - No dot indicator (visual noise removed)
 *   - Calmer top bar (lighter text weights)
 *   - Longer auto-scroll interval (10s — lets you read)
 *   - Minimal chrome — text is the UI
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onEventClick: (Int) -> Unit,
    onDatePickerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppBackground(modifier = modifier) {
        AnimatedContent(
            targetState = uiState,
            transitionSpec = {
                fadeIn(tween(400)) togetherWith fadeOut(tween(250))
            },
            contentKey = { it::class },
            label = "homeContent",
        ) { state ->
            when (state) {
                is HomeUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ReelShimmer()
                        TopBar(month = 0, day = 0, onDatePickerClick = onDatePickerClick)
                    }
                }

                is HomeUiState.Success -> {
                    ReelContent(
                        state = state,
                        onEventClick = onEventClick,
                        onDatePickerClick = onDatePickerClick,
                    )
                }

                is HomeUiState.Error -> {
                    ErrorContent(
                        state = state,
                        onRetry = { viewModel.loadEvents() },
                    )
                }
            }
        }
    }
}

@Composable
private fun ReelContent(
    state: HomeUiState.Success,
    onEventClick: (Int) -> Unit,
    onDatePickerClick: () -> Unit,
) {
    if (state.events.isEmpty()) {
        EmptyContent()
        return
    }

    val pagerState = rememberPagerState(pageCount = { state.events.size })

    // Auto-scroll: 10 seconds — enough time to read
    LaunchedEffect(pagerState, state.events.size) {
        snapshotFlow { pagerState.isScrollInProgress }.collect { scrolling ->
            if (!scrolling && state.events.size > 1) {
                delay(10_000)
                val next = (pagerState.currentPage + 1) % state.events.size
                pagerState.animateScrollToPage(
                    page = next,
                    animationSpec = tween(durationMillis = 1000),
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1,
        ) { page ->
            ReelPage(
                event = state.events[page],
                pageIndex = page,
                totalPages = state.events.size,
                isCurrentPage = pagerState.currentPage == page,
                onClick = { onEventClick(page) },
            )
        }

        // Minimal top bar
        TopBar(
            month = state.month,
            day = state.day,
            isRefreshing = state.isRefreshing,
            onDatePickerClick = onDatePickerClick,
        )
    }
}

@Composable
private fun TopBar(
    month: Int,
    day: Int,
    isRefreshing: Boolean = false,
    onDatePickerClick: () -> Unit,
) {
    val monthName = if (month in 1..12) {
        Month.of(month).getDisplayName(JavaTextStyle.SHORT, Locale.getDefault())
    } else ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "TODAY IN HISTORY",
                style = MaterialTheme.typography.labelSmall,
                color = PaperFaint.copy(alpha = 0.6f),
            )
            if (monthName.isNotEmpty()) {
                Text(
                    text = "$monthName $day",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                    color = Color.White.copy(alpha = 0.9f),
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 1.5.dp,
                    color = IceBlue.copy(alpha = 0.5f),
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            IconButton(
                onClick = onDatePickerClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.05f),
                ),
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = "Pick date",
                    tint = Color.White.copy(alpha = 0.5f),
                )
            }
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No events found",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.6f),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Nothing recorded for this date yet.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.3f),
            )
        }
    }
}

@Composable
private fun ErrorContent(
    state: HomeUiState.Error,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(40.dp),
        ) {
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.message,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(28.dp))
            TextButton(onClick = onRetry) {
                Text(
                    "Try again",
                    color = RoseGold.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
