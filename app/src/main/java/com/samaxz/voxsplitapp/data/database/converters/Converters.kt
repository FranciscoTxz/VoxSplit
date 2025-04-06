package com.samaxz.voxsplitapp.data.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.samaxz.voxsplitapp.domain.model.ResultInfo

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromResultInfo(resultInfo: ResultInfo): String {
        return gson.toJson(resultInfo)
    }

    @TypeConverter
    fun toResultInfo(json: String): ResultInfo {
        val type = object : TypeToken<ResultInfo>() {}.type
        return gson.fromJson(json, type)
    }
}
