package com.samaxz.voxsplitapp.data.network

import com.samaxz.voxsplitapp.data.model.ResultModel
import retrofit2.Response
import retrofit2.http.POST

interface ResultApiClient {
    @POST("/progress")
    suspend fun getSuperResult():Response<ResultModel>
}