package com.samaxz.voxsplitapp.domain.usecase

import com.samaxz.voxsplitapp.data.HistoryRepository
import javax.inject.Inject

class DeleteHistoryUseCase @Inject constructor(private val repository: HistoryRepository) {
    suspend operator fun invoke() {
        repository.clearHistory()
    }
}