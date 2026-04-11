package com.bajrangi.todayinhistory.data.remote.dto

import com.bajrangi.todayinhistory.domain.model.HistoricalEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Single event as returned by the backend API.
 *
 * Mapped to [HistoricalEvent] before leaving the data layer —
 * the rest of the app never sees this class.
 */
@Serializable
data class EventDto(
    val year: Int,
    val title: String,
    val description: String,
    @SerialName("wikipedia_url") val wikipediaUrl: String = "",
    @SerialName("ai_summary") val aiSummary: String = "",
    val month: Int = 0,
    val day: Int = 0,
)

/**
 * Paginated list response from:
 *   GET /api/events/today
 *   GET /api/events/{month}/{day}
 */
@Serializable
data class EventListResponse(
    val month: Int,
    val day: Int,
    val year: Int? = null,
    val total: Int,
    val skip: Int,
    val limit: Int,
    val events: List<EventDto>,
)

fun EventDto.toDomain() = HistoricalEvent(
    year = year,
    title = title,
    description = description,
    wikipediaUrl = wikipediaUrl,
    aiSummary = aiSummary,
    month = month,
    day = day,
)
