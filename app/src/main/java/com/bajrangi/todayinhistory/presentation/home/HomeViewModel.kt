package com.bajrangi.todayinhistory.presentation.home

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

    /** Currently displayed date — defaults to today. */
    private var currentMonth: Int = LocalDate.now().monthValue
    private var currentDay: Int = LocalDate.now().dayOfMonth

    /** Currently loaded events — used by the detail screen. */
    private var _events: List<HistoricalEvent> = emptyList()

    init {
        loadEvents()
    }

    /** Load events for the current date. Shows loading shimmer on first call. */
    fun loadEvents() {
        viewModelScope.launch {
            // Only show full loading state on the very first load.
            if (_uiState.value !is HomeUiState.Success) {
                _uiState.value = HomeUiState.Loading
            }

            getEventsUseCase(currentMonth, currentDay).collect { result ->
                result.fold(
                    onSuccess = { events ->
                        _events = events
                        _uiState.value = HomeUiState.Success(
                            events = events,
                            month = currentMonth,
                            day = currentDay,
                            isRefreshing = false,
                        )
                    },
                    onFailure = { error ->
                        // If we already have events showing, keep them
                        // and just stop the refresh indicator.
                        val current = _uiState.value
                        if (current is HomeUiState.Success && current.events.isNotEmpty()) {
                            _uiState.value = current.copy(isRefreshing = false)
                        } else {
                            _uiState.value = HomeUiState.Error(
                                message = error.localizedMessage
                                    ?: "Failed to load events",
                                month = currentMonth,
                                day = currentDay,
                            )
                        }
                    },
                )
            }
        }
    }

    /** Pull-to-refresh — shows the refresh indicator on existing content. */
    fun refresh() {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            _uiState.value = current.copy(isRefreshing = true)
        }
        loadEvents()
    }

    /** Switch to a different date. */
    fun selectDate(month: Int, day: Int) {
        if (month == currentMonth && day == currentDay) return
        currentMonth = month
        currentDay = day
        _uiState.value = HomeUiState.Loading
        loadEvents()
    }

    /** Get event by index for the detail screen. */
    fun getEvent(index: Int): HistoricalEvent? {
        return _events.getOrNull(index)
    }
}
