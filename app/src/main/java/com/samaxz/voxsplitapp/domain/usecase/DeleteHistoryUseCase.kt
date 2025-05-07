package com.samaxz.voxsplitapp.domain.usecase

import com.samaxz.voxsplitapp.data.APIDBRepository
import javax.inject.Inject

class DeleteHistoryUseCase @Inject constructor(private val repository: APIDBRepository) {
    suspend operator fun invoke() {
        repository.clearHistory()
    }
}