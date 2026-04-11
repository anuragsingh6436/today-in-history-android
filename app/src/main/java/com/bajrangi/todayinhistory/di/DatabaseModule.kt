package com.bajrangi.todayinhistory.di

import android.content.Context
import androidx.room.Room
import com.bajrangi.todayinhistory.data.local.dao.EventDao
import com.bajrangi.todayinhistory.data.local.db.HistoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HistoryDatabase {
        return Room.databaseBuilder(
            context,
            HistoryDatabase::class.java,
            "history_db",
        ).fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    fun provideEventDao(db: HistoryDatabase): EventDao = db.eventDao()
}
