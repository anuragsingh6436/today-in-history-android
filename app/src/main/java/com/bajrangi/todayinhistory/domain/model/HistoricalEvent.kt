package com.bajrangi.todayinhistory.domain.model

/**
 * Core domain model — UI and business logic only see this.
 * Completely independent of API DTOs and Room entities.
 */
data class HistoricalEvent(
    val year: Int,
    val title: String,
    val description: String,
    val wikipediaUrl: String = "",
    val thumbnailUrl: String = "",
    val aiSummary: String = "",
    val month: Int = 0,
    val day: Int = 0,
)
