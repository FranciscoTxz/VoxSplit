package com.samaxz.voxsplitapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.samaxz.voxsplitapp.data.database.dao.HistoryDao
import com.samaxz.voxsplitapp.data.database.entities.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 1)
abstract class HistoryDataBase : RoomDatabase() {
    abstract fun getHistoryDao(): HistoryDao
}