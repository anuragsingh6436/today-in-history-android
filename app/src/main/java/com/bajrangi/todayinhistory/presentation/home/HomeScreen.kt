package com.bajrangi.todayinhistory.presentation.home

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
import androidx.compose.material.icons.rounded.Settings
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
import com.bajrangi.todayinhistory.config.FeedMode
import com.bajrangi.todayinhistory.config.FeatureFlags
import com.bajrangi.todayinhistory.presentation.components.AppBackground
import com.bajrangi.todayinhistory.presentation.components.EventCard
import com.bajrangi.todayinhistory.presentation.components.FilterChipRow
import com.bajrangi.todayinhistory.presentation.components.ReelPage
import com.bajrangi.todayinhistory.presentation.components.ReelShimmer
import com.bajrangi.todayinhistory.presentation.components.ShimmerLoadingList
import com.bajrangi.todayinhistory.presentation.theme.IceBlue
import com.bajrangi.todayinhistory.presentation.theme.PaperFaint
import com.bajrangi.todayinhistory.presentation.theme.RoseGold
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale
import androidx.compose.animation.core.tween

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onEventClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppBackground(modifier = modifier) {
        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingContent(onSettingsClick = onSettingsClick)
            is HomeUiState.Success -> {
                when (FeatureFlags.feedMode) {
                    FeedMode.CARDS -> CardFeedContent(
                        state = state,
                        onEventClick = onEventClick,
                        onSettingsClick = onSettingsClick,
                        onRefresh = { viewModel.refresh() },
                        onRegionSelect = { viewModel.selectRegion(it) },
                        onCategorySelect = { viewModel.selectCategory(it) },
                    )
                    FeedMode.REELS -> ReelFeedContent(
                        state = state,
                        onEventClick = onEventClick,
                        onSettingsClick = onSettingsClick,
                        onRegionSelect = { viewModel.selectRegion(it) },
                        onCategorySelect = { viewModel.selectCategory(it) },
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
private fun LoadingContent(onSettingsClick: () -> Unit) {
    when (FeatureFlags.feedMode) {
        FeedMode.CARDS -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
            ) {
                val today = LocalDate.now()
                DateHeader(
                    month = today.monthValue,
                    day = today.dayOfMonth,
                    onSettingsClick = onSettingsClick,
                )
                ShimmerLoadingList(modifier = Modifier.padding(top = 12.dp))
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
    onSettingsClick: () -> Unit,
    onRefresh: () -> Unit,
    onRegionSelect: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item(key = "header") {
            DateHeader(
                month = state.month,
                day = state.day,
                eventCount = state.filteredEvents.size,
                totalCount = state.events.size,
                isRefreshing = state.isRefreshing,
                onRefresh = onRefresh,
                onSettingsClick = onSettingsClick,
            )
        }

        // Filter chips — always show if any categories/regions exist
        if (state.availableRegions.size > 1 || state.availableCategories.size > 1) {
            item(key = "filters") {
                Column {
                    if (state.availableRegions.size > 1) {
                        FilterChipRow(
                            label = "REGION",
                            options = state.availableRegions,
                            selected = state.selectedRegion,
                            onSelect = onRegionSelect,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    if (state.availableCategories.size > 1) {
                        FilterChipRow(
                            label = "CATEGORY",
                            options = state.availableCategories,
                            selected = state.selectedCategory,
                            onSelect = onCategorySelect,
                        )
                    }
                }
            }
        }

        itemsIndexed(
            items = state.filteredEvents,
            key = { _, event -> "${event.year}_${event.title}" },
        ) { index, event ->
            EventCard(
                event = event,
                index = index,
                onClick = { onEventClick(index) },
            )
        }

        item(key = "spacer") { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

// ── Reels Mode ──────────────────────────────────────────────────

@Composable
private fun ReelFeedContent(
    state: HomeUiState.Success,
    onEventClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onRegionSelect: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
) {
    val events = state.filteredEvents
    if (events.isEmpty()) {
        EmptyContent()
        return
    }

    val pagerState = rememberPagerState(pageCount = { events.size })

    LaunchedEffect(pagerState, events.size) {
        snapshotFlow { pagerState.isScrollInProgress }.collect { scrolling ->
            if (!scrolling && events.size > 1) {
                delay(10_000)
                val next = (pagerState.currentPage + 1) % events.size
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
                event = events[page],
                pageIndex = page,
                totalPages = events.size,
                isCurrentPage = pagerState.currentPage == page,
                onClick = { onEventClick(page) },
            )
        }

        // Top overlay — settings + filter chips
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 8.dp),
        ) {
            // Settings row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Filter chips
            if (state.availableRegions.size > 1 || state.availableCategories.size > 1) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    if (state.availableRegions.size > 1) {
                        FilterChipRow(
                            label = "REGION",
                            options = state.availableRegions,
                            selected = state.selectedRegion,
                            onSelect = onRegionSelect,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (state.availableCategories.size > 1) {
                        FilterChipRow(
                            label = "CATEGORY",
                            options = state.availableCategories,
                            selected = state.selectedCategory,
                            onSelect = onCategorySelect,
                        )
                    }
                }
            }
        }
    }
}

// ── Header ──────────────────────────────────────────────────────

@Composable
private fun DateHeader(
    month: Int,
    day: Int,
    eventCount: Int = 0,
    totalCount: Int = 0,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
) {
    val monthName = if (month in 1..12) {
        Month.of(month).getDisplayName(JavaTextStyle.FULL, Locale.getDefault())
    } else ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 16.dp),
    ) {
        // Top row: label + settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "TODAY IN HISTORY",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = IceBlue.copy(alpha = 0.45f),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 1.5.dp,
                        color = IceBlue.copy(alpha = 0.4f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else if (onRefresh != null) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(36.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                if (onSettingsClick != null) {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Date — large display
        Text(
            text = if (monthName.isNotEmpty()) "$monthName $day" else "",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )

        // Event count
        if (eventCount > 0) {
            Spacer(modifier = Modifier.height(6.dp))
            val countText = if (totalCount > 0 && eventCount != totalCount) {
                "$eventCount of $totalCount events"
            } else {
                "$eventCount events on this day"
            }
            Text(
                text = countText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

// ── Empty & Error ───────────────────────────────────────────────

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
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Nothing recorded for this date yet.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
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
