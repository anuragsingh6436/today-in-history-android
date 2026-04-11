package com.bajrangi.todayinhistory.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bajrangi.todayinhistory.config.FeedMode
import com.bajrangi.todayinhistory.config.FeatureFlags
import com.bajrangi.todayinhistory.presentation.components.AppBackground
import com.bajrangi.todayinhistory.presentation.components.EventCard
import com.bajrangi.todayinhistory.presentation.components.ReelPage
import com.bajrangi.todayinhistory.presentation.components.ReelShimmer
import com.bajrangi.todayinhistory.presentation.components.ShimmerLoadingList
import com.bajrangi.todayinhistory.presentation.theme.IceBlue
import com.bajrangi.todayinhistory.presentation.theme.PaperFaint
import com.bajrangi.todayinhistory.presentation.theme.RoseGold
import kotlinx.coroutines.delay
import java.time.Month
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onEventClick: (Int) -> Unit,
    onDatePickerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppBackground(modifier = modifier) {
        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingContent()

            is HomeUiState.Success -> {
                when (FeatureFlags.feedMode) {
                    FeedMode.CARDS -> CardFeedContent(
                        state = state,
                        onEventClick = onEventClick,
                        onRefresh = { viewModel.refresh() },
                    )
                    FeedMode.REELS -> ReelFeedContent(
                        state = state,
                        onEventClick = onEventClick,
                    )
                }
            }

            is HomeUiState.Error -> ErrorContent(
                state = state,
                onRetry = { viewModel.loadEvents() },
            )
        }
    }
}

// ── Loading ─────────────────────────────────────────────────────

@Composable
private fun LoadingContent() {
    when (FeatureFlags.feedMode) {
        FeedMode.CARDS -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            ) {
                DateHeader(month = 0, day = 0)
                ShimmerLoadingList(modifier = Modifier.padding(top = 8.dp))
            }
        }
        FeedMode.REELS -> {
            Box(modifier = Modifier.fillMaxSize()) {
                ReelShimmer()
            }
        }
    }
}

// ── Cards Mode ──────────────────────────────────────────────────

@Composable
private fun CardFeedContent(
    state: HomeUiState.Success,
    onEventClick: (Int) -> Unit,
    onRefresh: () -> Unit,
) {
    // Preserve scroll position across recompositions
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "header") {
            DateHeader(
                month = state.month,
                day = state.day,
                eventCount = state.events.size,
                isRefreshing = state.isRefreshing,
                onRefresh = onRefresh,
            )
        }

        itemsIndexed(
            items = state.events,
            key = { _, event -> "${event.year}_${event.title}" },
        ) { index, event ->
            EventCard(
                event = event,
                index = index,
                onClick = { onEventClick(index) },
            )
        }

        item(key = "spacer") { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

// ── Reels Mode ──────────────────────────────────────────────────

@Composable
private fun ReelFeedContent(
    state: HomeUiState.Success,
    onEventClick: (Int) -> Unit,
) {
    if (state.events.isEmpty()) {
        EmptyContent()
        return
    }

    val pagerState = rememberPagerState(pageCount = { state.events.size })

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
    }
}

// ── Shared Components ───────────────────────────────────────────

@Composable
private fun DateHeader(
    month: Int,
    day: Int,
    eventCount: Int = 0,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
) {
    val monthName = if (month in 1..12) {
        Month.of(month).getDisplayName(JavaTextStyle.FULL, Locale.getDefault())
    } else ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp),
    ) {
        Text(
            text = "TODAY IN HISTORY",
            style = MaterialTheme.typography.labelSmall,
            color = PaperFaint.copy(alpha = 0.5f),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (monthName.isNotEmpty()) "$monthName $day" else "",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (onRefresh != null) {
                IconButton(onClick = onRefresh) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 1.5.dp,
                            color = IceBlue.copy(alpha = 0.5f),
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        )
                    }
                }
            }
        }

        if (eventCount > 0) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$eventCount events on this day",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
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
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
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
