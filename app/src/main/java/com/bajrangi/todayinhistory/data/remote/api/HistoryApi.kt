package com.bajrangi.todayinhistory.data.remote.api

import com.bajrangi.todayinhistory.data.remote.dto.EventListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the Today in History backend.
 *
 * Every method is a suspend function — Retrofit handles the
 * coroutine dispatch automatically.
 */
interface HistoryApi {

    @GET("api/events/today")
    suspend fun getTodayEvents(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
    ): EventListResponse

    @GET("api/events/{month}/{day}")
    suspend fun getEventsByDate(
        @Path("month") month: Int,
        @Path("day") day: Int,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
    ): EventListResponse
}
