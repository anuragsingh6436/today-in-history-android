package com.bajrangi.todayinhistory.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bajrangi.todayinhistory.data.local.entity.EventEntity

@Dao
interface EventDao {

    @Query("SELECT * FROM events WHERE month = :month AND day = :day ORDER BY year ASC")
    suspend fun getEventsByDate(month: Int, day: Int): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    @Query("DELETE FROM events WHERE month = :month AND day = :day")
    suspend fun deleteByDate(month: Int, day: Int)

    /** Returns the most recent cachedAt timestamp for a given date, or null if empty. */
    @Query("SELECT MAX(cachedAt) FROM events WHERE month = :month AND day = :day")
    suspend fun getLastCachedAt(month: Int, day: Int): Long?
}
