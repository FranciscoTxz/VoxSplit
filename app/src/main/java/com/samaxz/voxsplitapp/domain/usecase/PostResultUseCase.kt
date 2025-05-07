package com.samaxz.voxsplitapp.domain.usecase

import com.samaxz.voxsplitapp.data.APIDBRepository
import com.samaxz.voxsplitapp.domain.model.ResultInfo
import java.io.File
import javax.inject.Inject

class PostResultUseCase @Inject constructor(private val repository: APIDBRepository) {
    suspend operator fun invoke(
        speakers: Int,
        language: String,
        file: File
    ): ResultInfo? {
        val response = repository.getResultFromApi(speakers, language, file)
        return response
    }
}