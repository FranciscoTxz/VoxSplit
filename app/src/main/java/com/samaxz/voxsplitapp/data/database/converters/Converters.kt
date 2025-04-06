package com.samaxz.voxsplitapp.data.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.samaxz.voxsplitapp.data.database.entities.ResultEntity

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromResultEntity(resultEntity: ResultEntity): String {
        return gson.toJson(resultEntity)
    }

    @TypeConverter
    fun toResultEntity(json: String): ResultEntity {
        val type = object : TypeToken<ResultEntity>() {}.type
        return gson.fromJson(json, type)
    }
}
