package com.bajrangi.todayinhistory.data.local.entity

import androidx.room.Entity
import com.bajrangi.todayinhistory.data.remote.dto.EventDto
import com.bajrangi.todayinhistory.domain.model.HistoricalEvent

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
    val thumbnailUrl: String = "",
    val aiSummary: String = "",
    val category: String = "",
    val region: String = "",
    val cachedAt: Long = System.currentTimeMillis(),
)

fun EventEntity.toDomain() = HistoricalEvent(
    year = year,
    title = title,
    description = description,
    wikipediaUrl = wikipediaUrl,
    thumbnailUrl = thumbnailUrl,
    aiSummary = aiSummary,
    category = category,
    region = region,
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
    thumbnailUrl = thumbnailUrl,
    aiSummary = aiSummary,
    category = category,
    region = region,
)
