package com.samaxz.voxsplitapp.data.network

import android.util.Log
import com.samaxz.voxsplitapp.data.model.ResultModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class ResultService @Inject constructor(private val api: ResultApiClient) {

    suspend fun getResult(speakers: Int, language: String, file: File): ResultModel? {
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = RequestBody.create(MediaType.parse("audio/*"), file)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val response = api.getSuperResult(intParam = speakers, stringParam = language, file = filePart)
                Log.i("SUPERSAMA", "Raw Response: $response")
                response.body()
            } catch (e: Exception) {
                Log.i("SUPERSAMA", "Exception: $e")
                null
            }
        }
    }
}