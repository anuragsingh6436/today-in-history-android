package com.bajrangi.todayinhistory.presentation.home

import com.bajrangi.todayinhistory.domain.model.HistoricalEvent

/**
 * Sealed UI state for the Home screen.
 *
 * Each subclass maps to a distinct visual state — the Compose UI
 * renders exactly one branch with no ambiguous boolean checks.
 */
sealed interface HomeUiState {

    /** Initial load — show shimmer skeleton. */
    data object Loading : HomeUiState

    /** Events loaded successfully. */
    data class Success(
        val events: List<HistoricalEvent>,
        val month: Int,
        val day: Int,
        /** True while a silent background refresh is in progress. */
        val isRefreshing: Boolean = false,
    ) : HomeUiState

    /**
     * Something went wrong.
     *
     * [cachedEvents] may be non-empty if stale cache is available —
     * the UI can still show content alongside the error banner.
     */
    data class Error(
        val message: String,
        val cachedEvents: List<HistoricalEvent> = emptyList(),
        val month: Int = 0,
        val day: Int = 0,
    ) : HomeUiState
}
