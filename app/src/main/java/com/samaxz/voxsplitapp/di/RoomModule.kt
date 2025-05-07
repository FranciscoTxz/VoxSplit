package com.samaxz.voxsplitapp.di

import android.content.Context
import androidx.room.Room
import com.samaxz.voxsplitapp.data.database.HistoryDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {
    companion object {
        private const val QUOTE_DATABASE_NAME = "history_db"
    }

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, HistoryDataBase::class.java, QUOTE_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideQuoteDao(db: HistoryDataBase) = db.getHistoryDao()
}