package com.samaxz.voxsplitapp.domain.model

import com.samaxz.voxsplitapp.data.database.entities.HistoryEntity

data class HistoryInfo(
    val id: Int?,
    val name: String,
    val uri: String,
    val speakers: String,
    val language: String,
    val size: String,
    val time: String,
    val description: String
)

fun HistoryEntity.toDomain() =
    HistoryInfo(
        id = id,
        name = name,
        uri = uri,
        speakers = speakers,
        language = language,
        size = size,
        time = time,
        description = description
    )