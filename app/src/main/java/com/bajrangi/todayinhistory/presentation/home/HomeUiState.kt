package com.bajrangi.todayinhistory.presentation.home

import com.bajrangi.todayinhistory.domain.model.HistoricalEvent

sealed interface HomeUiState {

    data object Loading : HomeUiState

    data class Success(
        val events: List<HistoricalEvent>,
        /** Events after applying filters. */
        val filteredEvents: List<HistoricalEvent>,
        val month: Int,
        val day: Int,
        val isRefreshing: Boolean = false,
        val selectedRegion: String = "All",
        val selectedCategory: String = "All",
        val availableRegions: List<String> = emptyList(),
        val availableCategories: List<String> = emptyList(),
    ) : HomeUiState

    data class Error(
        val message: String,
        val cachedEvents: List<HistoricalEvent> = emptyList(),
        val month: Int = 0,
        val day: Int = 0,
    ) : HomeUiState
}
