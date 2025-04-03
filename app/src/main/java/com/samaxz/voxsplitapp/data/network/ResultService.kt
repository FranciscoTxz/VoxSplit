package com.samaxz.voxsplitapp.data.network

import android.util.Log
import com.samaxz.voxsplitapp.data.model.ResultModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResultService @Inject constructor(private val api: ResultApiClient) {

    suspend fun getResult(): ResultModel? {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getSuperResult()
                Log.i("SUPERSAMA", "Raw Response: ${response.code()}")
                response.body()
            } catch (e: Exception) {
                Log.i("SUPERSAMA", "Exception: $e")
                null
            }
        }
    }
}