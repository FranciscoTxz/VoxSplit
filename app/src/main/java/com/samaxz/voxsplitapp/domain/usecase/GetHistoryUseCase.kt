package com.samaxz.voxsplitapp.domain.usecase

import com.samaxz.voxsplitapp.data.HistoryRepository
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository,
//    private val historyProvider: HistoryProvider
) {

    suspend operator fun invoke(): List<HistoryInfo> {
        //val quotes = repository.getAllQuotesFromApi()
//        val history = repository.getAllHistoryFromDataBase()

//        if (history.isEmpty()) {
//            var response = historyProvider.getHistory()
//            var response2 = response.map { it.toDatabase() }
//            repository.insertHistory(response2)
//
//            return repository.getAllHistoryFromDataBase()
//        } else {
//            return repository.getAllHistoryFromDataBase()
//        }

        return repository.getAllHistoryFromDataBase()
    }
}