package com.samaxz.voxsplitapp.domain.usecase

import com.samaxz.voxsplitapp.data.APIDBRepository
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: APIDBRepository,
) {

    suspend operator fun invoke(): List<HistoryInfo> {
        return repository.getAllHistoryFromDataBase()
    }
}