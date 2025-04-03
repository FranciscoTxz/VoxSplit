package com.samaxz.voxsplitapp.data.network

import com.samaxz.voxsplitapp.data.model.ResultModel
import retrofit2.Response
import retrofit2.http.GET

interface ResultApiClient {
    @GET("/.json")
    suspend fun getSuperResult():Response<ResultModel>
}