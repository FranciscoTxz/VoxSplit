package com.samaxz.voxsplitapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.samaxz.voxsplitapp.domain.model.HistoryInfo

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "uri") val uri: String,
    @ColumnInfo(name = "speakers") val speakers: String,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "size") val size: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "description") val description: String
)

fun HistoryInfo.toDatabase() =
    HistoryEntity(
        name = name,
        uri = uri,
        speakers = speakers,
        language = language,
        size = size,
        time = time,
        description = description
    )