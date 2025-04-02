package com.samaxz.voxsplitapp.domain.model

import com.samaxz.voxsplitapp.data.database.entities.HistoryEntity
import com.samaxz.voxsplitapp.data.model.HistoryModel

data class HistoryInfo(val name: String, val description: String)

fun HistoryModel.toDomain() = HistoryInfo(name, description)
fun HistoryEntity.toDomain() = HistoryInfo(name, description)