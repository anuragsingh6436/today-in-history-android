package com.bajrangi.todayinhistory.presentation.detail

import com.bajrangi.todayinhistory.domain.model.HistoricalEvent

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val event: HistoricalEvent) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
