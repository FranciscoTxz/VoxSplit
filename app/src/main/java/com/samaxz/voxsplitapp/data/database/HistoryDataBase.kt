package com.samaxz.voxsplitapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.samaxz.voxsplitapp.data.database.converters.Converters
import com.samaxz.voxsplitapp.data.database.dao.HistoryDao
import com.samaxz.voxsplitapp.data.database.entities.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class HistoryDataBase : RoomDatabase() {
    abstract fun getHistoryDao(): HistoryDao
}