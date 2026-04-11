package com.bajrangi.todayinhistory.data.local.entity

import androidx.room.Entity
import com.bajrangi.todayinhistory.data.remote.dto.EventDto
import com.bajrangi.todayinhistory.domain.model.HistoricalEvent

/**
 * Room entity for cached historical events.
 *
 * Composite primary key on (year, month, day, title) prevents duplicates
 * — mirrors the backend's UNIQUE constraint.
 */
@Entity(
    tableName = "events",
    primaryKeys = ["year", "month", "day", "title"],
)
data class EventEntity(
    val year: Int,
    val month: Int,
    val day: Int,
    val title: String,
    val description: String,
    val wikipediaUrl: String = "",
    val aiSummary: String = "",
    /** Epoch millis when this row was last refreshed from the API. */
    val cachedAt: Long = System.currentTimeMillis(),
)

fun EventEntity.toDomain() = HistoricalEvent(
    year = year,
    title = title,
    description = description,
    wikipediaUrl = wikipediaUrl,
    aiSummary = aiSummary,
    month = month,
    day = day,
)

fun EventDto.toEntity() = EventEntity(
    year = year,
    month = month,
    day = day,
    title = title,
    description = description,
    wikipediaUrl = wikipediaUrl,
    aiSummary = aiSummary,
)
