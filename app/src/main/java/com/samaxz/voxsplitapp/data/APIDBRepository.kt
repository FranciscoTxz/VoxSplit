package com.samaxz.voxsplitapp.data

import com.samaxz.voxsplitapp.data.database.dao.HistoryDao
import com.samaxz.voxsplitapp.data.database.entities.HistoryEntity
import com.samaxz.voxsplitapp.data.model.ResultModel
import com.samaxz.voxsplitapp.data.network.ResultService
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import com.samaxz.voxsplitapp.domain.model.ResultInfo
import com.samaxz.voxsplitapp.domain.model.toDomain
import javax.inject.Inject
import kotlin.collections.map

class APIDBRepository @Inject constructor(
    private val api: ResultService,
    private val historyDao: HistoryDao
) {
    suspend fun getResultFromApi(): ResultInfo? {
        val response: ResultModel? = api.getResult()
        return response?.toDomain()
    }

    suspend fun getAllHistoryFromDataBase(): List<HistoryInfo> {
        val response = historyDao.getAllHistory()
        return response.map { it.toDomain() }
    }


    suspend fun insertOneHistory(history: HistoryEntity) {
        historyDao.insertOne(history)
    }

    suspend fun clearHistory() {
        historyDao.deleteAllHistory()
    }
}