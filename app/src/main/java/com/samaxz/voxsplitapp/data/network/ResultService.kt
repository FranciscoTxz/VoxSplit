package com.samaxz.voxsplitapp.data.network

import com.samaxz.voxsplitapp.data.model.ResultModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResultService @Inject constructor(private val api: ResultApiClient) {

    suspend fun getResult(): ResultModel? {
        return withContext(Dispatchers.IO) {
            val response = api.getSuperResult()
            response.body()
        }
    }
}