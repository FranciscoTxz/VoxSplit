package com.samaxz.voxsplitapp.domain.usecase

import com.samaxz.voxsplitapp.data.APIDBRepository
import com.samaxz.voxsplitapp.domain.model.ResultInfo
import javax.inject.Inject

class POSTResultUseCase @Inject constructor(private val repository: APIDBRepository) {
    suspend operator fun invoke(): ResultInfo? {
        val response = repository.getResultFromApi()
        return response
    }
}