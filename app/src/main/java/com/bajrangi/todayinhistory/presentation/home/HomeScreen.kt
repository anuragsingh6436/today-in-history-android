package com.bajrangi.todayinhistory.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bajrangi.todayinhistory.presentation.components.AppBackground
import com.bajrangi.todayinhistory.presentation.components.EventCard
import com.bajrangi.todayinhistory.presentation.components.ShimmerLoadingList
import com.bajrangi.todayinhistory.presentation.theme.IceBlue
import com.bajrangi.todayinhistory.presentation.theme.PaperFaint
import com.bajrangi.todayinhistory.presentation.theme.RoseGold
import java.time.Month
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

/**
 * Home screen — scrollable card feed.
 *
 * Events with images get large image cards (Google Discover style).
 * Events without images get clean text-only glass cards.
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding(),
                    ) {
                        DateHeader(month = 0, day = 0)
                        ShimmerLoadingList(modifier = Modifier.padding(top = 8.dp))
                    }
                }

                is HomeUiState.Success -> {
                    FeedContent(
                        state = state,
                        onEventClick = onEventClick,
                        onRefresh = { viewModel.refresh() },
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
private fun FeedContent(
    state: HomeUiState.Success,
    onEventClick: (Int) -> Unit,
    onRefresh: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Date header
        item {
            DateHeader(
                month = state.month,
                day = state.day,
                eventCount = state.events.size,
                isRefreshing = state.isRefreshing,
                onRefresh = onRefresh,
            )
        }

        // Event cards
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

        // Bottom spacer
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

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
        // App label
        Text(
            text = "TODAY IN HISTORY",
            style = MaterialTheme.typography.labelSmall,
            color = PaperFaint.copy(alpha = 0.5f),
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Date + refresh
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

        // Event count
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
