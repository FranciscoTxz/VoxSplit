package com.samaxz.voxsplitapp.data

import com.samaxz.voxsplitapp.data.database.dao.HistoryDao
import com.samaxz.voxsplitapp.data.database.entities.HistoryEntity
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import com.samaxz.voxsplitapp.domain.model.toDomain
import javax.inject.Inject
import kotlin.collections.map

class HistoryRepository @Inject constructor(
//    private val api: History service,
    private val historyDao: HistoryDao
) {
//    suspend fun getAllHistoryFromApi(): List<Quote> {
//        val response: List<QuoteModel> = api.getHistory()
//        return response.map { it.toDomain() }
//    }

    suspend fun getAllHistoryFromDataBase(): List<HistoryInfo> {
        val response = historyDao.getAllHistory()
        return response.map { it.toDomain() }
    }

    suspend fun insertHistory(history: List<HistoryEntity>) {
        historyDao.insertAll(history)
    }

    suspend fun insertOneHistory(history: HistoryEntity) {
        historyDao.insertOne(history)
    }

    suspend fun clearHistory() {
        historyDao.deleteAllHistory()
    }
}