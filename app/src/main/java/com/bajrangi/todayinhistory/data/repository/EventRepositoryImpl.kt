package com.bajrangi.todayinhistory.data.repository

import com.bajrangi.todayinhistory.data.local.dao.EventDao
import com.bajrangi.todayinhistory.data.local.entity.EventEntity
import com.bajrangi.todayinhistory.data.local.entity.toDomain
import com.bajrangi.todayinhistory.data.local.entity.toEntity
import com.bajrangi.todayinhistory.data.remote.api.HistoryApi
import com.bajrangi.todayinhistory.domain.model.HistoricalEvent
import com.bajrangi.todayinhistory.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first repository.
 *
 * Strategy:
 *   1. Emit cached data immediately (if any).
 *   2. If cache is stale (> [CACHE_MAX_AGE_MS]), fetch from API.
 *   3. Store fresh data in Room, emit updated list.
 *   4. If network fails but cache exists, keep showing cache.
 *   5. If network fails and no cache, emit the error.
 */
@Singleton
class EventRepositoryImpl @Inject constructor(
    private val api: HistoryApi,
    private val dao: EventDao,
) : EventRepository {

    override fun getEvents(month: Int, day: Int): Flow<Result<List<HistoricalEvent>>> = flow {
        // 1. Emit cache immediately.
        val cached = dao.getEventsByDate(month, day)
        if (cached.isNotEmpty()) {
            emit(Result.success(cached.map(EventEntity::toDomain)))
        }

        // 2. Check freshness.
        val lastCached = dao.getLastCachedAt(month, day)
        val isStale = lastCached == null ||
            (System.currentTimeMillis() - lastCached) > CACHE_MAX_AGE_MS

        if (!isStale) return@flow

        // 3. Fetch from network.
        try {
            val response = api.getEventsByDate(month = month, day = day)
            val entities = response.events.map { it.toEntity() }

            // Replace stale cache atomically.
            dao.deleteByDate(month, day)
            dao.insertAll(entities)

            // 4. Emit fresh data.
            val fresh = dao.getEventsByDate(month, day)
            emit(Result.success(fresh.map(EventEntity::toDomain)))
        } catch (e: Exception) {
            // 5. Network failed — if we already emitted cache, don't
            // emit an error (user sees stale data, which is fine).
            // If no cache existed, emit the error.
            if (cached.isEmpty()) {
                emit(Result.failure(e))
            }
        }
    }

    companion object {
        /** Cache is considered fresh for 24 hours. */
        const val CACHE_MAX_AGE_MS = 24 * 60 * 60 * 1000L
    }
}
