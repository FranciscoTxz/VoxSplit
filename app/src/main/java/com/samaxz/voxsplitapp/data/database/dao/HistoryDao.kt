package com.samaxz.voxsplitapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.samaxz.voxsplitapp.data.database.entities.HistoryEntity

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history_table ORDER BY id DESC ")
    suspend fun getAllHistory(): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(history: HistoryEntity)

    @Query("DELETE FROM history_table")
    suspend fun deleteAllHistory()
}