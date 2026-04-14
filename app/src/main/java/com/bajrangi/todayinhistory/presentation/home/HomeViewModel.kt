package com.bajrangi.todayinhistory.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bajrangi.todayinhistory.domain.model.HistoricalEvent
import com.bajrangi.todayinhistory.domain.usecase.GetEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentMonth: Int = LocalDate.now().monthValue
    private var currentDay: Int = LocalDate.now().dayOfMonth

    /** Full unfiltered list — used for detail screen lookup. */
    private var _allEvents: List<HistoricalEvent> = emptyList()
    /** Currently displayed filtered list — detail uses this for index. */
    private var _filteredEvents: List<HistoricalEvent> = emptyList()

    private var selectedRegion = "All"
    private var selectedCategory = "All"

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            if (_uiState.value !is HomeUiState.Success) {
                _uiState.value = HomeUiState.Loading
            }

            getEventsUseCase(currentMonth, currentDay).collect { result ->
                result.fold(
                    onSuccess = { events ->
                        _allEvents = events
                        emitFiltered(events)
                    },
                    onFailure = { error ->
                        val current = _uiState.value
                        if (current is HomeUiState.Success && current.events.isNotEmpty()) {
                            _uiState.value = current.copy(isRefreshing = false)
                        } else {
                            _uiState.value = HomeUiState.Error(
                                message = error.localizedMessage ?: "Failed to load events",
                                month = currentMonth,
                                day = currentDay,
                            )
                        }
                    },
                )
            }
        }
    }

    fun refresh() {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            _uiState.value = current.copy(isRefreshing = true)
        }
        loadEvents()
    }

    fun selectRegion(region: String) {
        selectedRegion = region
        emitFiltered(_allEvents)
    }

    fun selectCategory(category: String) {
        selectedCategory = category
        emitFiltered(_allEvents)
    }

    fun selectDate(month: Int, day: Int) {
        if (month == currentMonth && day == currentDay) return
        currentMonth = month
        currentDay = day
        selectedRegion = "All"
        selectedCategory = "All"
        _uiState.value = HomeUiState.Loading
        loadEvents()
    }

    /** Get event by index from the FILTERED list. */
    fun getEvent(index: Int): HistoricalEvent? {
        return _filteredEvents.getOrNull(index)
    }

    private fun emitFiltered(events: List<HistoricalEvent>) {
        Log.d("HomeVM", "emitFiltered: ${events.size} events")
        Log.d("HomeVM", "Sample categories: ${events.take(5).map { it.category }}")
        Log.d("HomeVM", "Sample regions: ${events.take(5).map { it.region }}")

        // Extract available filters from data
        val regions = listOf("All") + events
            .map { it.region }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        val categories = listOf("All") + events
            .map { it.category }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        Log.d("HomeVM", "Available regions: $regions")
        Log.d("HomeVM", "Available categories: $categories")

        // Apply filters
        val filtered = events.filter { event ->
            val regionMatch = selectedRegion == "All" || event.region == selectedRegion
            val categoryMatch = selectedCategory == "All" || event.category == selectedCategory
            regionMatch && categoryMatch
        }

        _filteredEvents = filtered

        _uiState.value = HomeUiState.Success(
            events = events,
            filteredEvents = filtered,
            month = currentMonth,
            day = currentDay,
            isRefreshing = false,
            selectedRegion = selectedRegion,
            selectedCategory = selectedCategory,
            availableRegions = regions,
            availableCategories = categories,
        )
    }
}
