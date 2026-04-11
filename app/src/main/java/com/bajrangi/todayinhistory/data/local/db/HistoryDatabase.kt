package com.bajrangi.todayinhistory.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bajrangi.todayinhistory.data.local.dao.EventDao
import com.bajrangi.todayinhistory.data.local.entity.EventEntity

@Database(
    entities = [EventEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}
