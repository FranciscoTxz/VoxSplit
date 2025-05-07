package com.samaxz.voxsplitapp.domain.usecase

import com.samaxz.voxsplitapp.data.APIDBRepository
import com.samaxz.voxsplitapp.data.database.entities.toDatabase
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import javax.inject.Inject

class PostNewHistoryUseCase @Inject constructor(private val repository: APIDBRepository) {
    suspend operator fun invoke(history: HistoryInfo) {
        repository.insertOneHistory(history.toDatabase())
    }
}