package com.samaxz.voxsplitapp.data.network

import com.samaxz.voxsplitapp.data.model.ResultModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ResultApiClient {
    @Multipart
    @POST("/process")
    suspend fun getSuperResult(
        @Header("speakers") intParam: Int,
        @Header("language") stringParam: String,
        @Part file: MultipartBody.Part
    ): Response<ResultModel>
}