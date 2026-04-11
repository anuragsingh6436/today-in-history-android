package com.bajrangi.todayinhistory.domain.repository

import com.bajrangi.todayinhistory.domain.model.HistoricalEvent
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract — the domain layer depends on this interface,
 * not on Retrofit or Room directly.
 */
interface EventRepository {
    /**
     * Observe events for a given date.
     *
     * Emits cached data first (if available), then refreshes from
     * the network and emits updated data.
     */
    fun getEvents(month: Int, day: Int): Flow<Result<List<HistoricalEvent>>>
}
