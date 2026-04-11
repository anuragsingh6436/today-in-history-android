package com.bajrangi.todayinhistory.domain.usecase

import com.bajrangi.todayinhistory.domain.model.HistoricalEvent
import com.bajrangi.todayinhistory.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Fetches historical events for a given date.
 *
 * Thin use case — delegates to the repository. Exists so the
 * presentation layer never depends on the repository directly,
 * and so we have a natural place to add business rules later
 * (e.g. filtering, sorting, limiting).
 */
class GetEventsUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    operator fun invoke(month: Int, day: Int): Flow<Result<List<HistoricalEvent>>> {
        return repository.getEvents(month, day)
    }
}
