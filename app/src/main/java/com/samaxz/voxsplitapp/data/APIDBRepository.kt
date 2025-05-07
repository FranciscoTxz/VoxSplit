package com.samaxz.voxsplitapp.data

import android.util.Log
import com.samaxz.voxsplitapp.data.database.dao.HistoryDao
import com.samaxz.voxsplitapp.data.database.entities.HistoryEntity
import com.samaxz.voxsplitapp.data.model.ResultModel
import com.samaxz.voxsplitapp.data.network.ResultService
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import com.samaxz.voxsplitapp.domain.model.ResultInfo
import com.samaxz.voxsplitapp.domain.model.toDomain
import java.io.File
import javax.inject.Inject
import kotlin.collections.map

class APIDBRepository @Inject constructor(
    private val api: ResultService,
    private val historyDao: HistoryDao
) {
    suspend fun getResultFromApi(
        speakers: Int,
        language: String,
        file: File
    ): ResultInfo? {
        val response: ResultModel? = api.getResult(speakers, language, file)
        return response?.toDomain()
    }

    suspend fun getAllHistoryFromDataBase(): List<HistoryInfo> {
        val response = historyDao.getAllHistory()
        return response.map { it.toDomain() }
    }

    suspend fun getOneHistoryFromDataBase(id: Int): HistoryInfo? {
        val response = historyDao.getHistoryById(id)?.toDomain()
        return response
    }

    suspend fun insertOneHistory(history: HistoryEntity) {
        historyDao.insertOne(history)
    }

    suspend fun clearHistory() {
        historyDao.deleteAllHistory()
    }
}