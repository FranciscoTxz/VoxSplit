package com.samaxz.voxsplitapp.data.providers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import javax.inject.Inject

class HistoryProvider @Inject constructor() {
    fun getHistory(): List<HistoryInfo> {
        val jsonString = """[
            {"name": "x3.wav", "description": "XONEX XONEX XONEX XONEX XONEX XONEX XONEX XONEX XONEX XONEX "},
            {"name": "x4.wav", "description": "XTWOX XTWOX XTWOX XTWOX XTWOX XTWOX XTWOX XTWOX XTWOX XTWOX "},
            {"name": "audio.wav", "description": "XTHREEX XTHREEX XTHREEX XTHREEX XTHREEX XTHREEX XTHREEX XTHREEX "}
        ]"""

        val listType = object : TypeToken<List<HistoryInfo>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }
    fun getHistory2(): List<HistoryInfo> {
        val jsonString = """[
            {"name": "x3112.wav", "description": "XONEX XONEX XONEX XONEX XONEX XONEX XONEX XONEX XONEX XONEX "},
            {"name": "x4123.wav", "description": "XTWOX XTWOX XTWOX XTWOX XTWOX XTWOX XTWOX XTWOX XTWOX XTWOX "},
            {"name": "audio1213.wav", "description": "XTHREEX XTHREEX XTHREEX XTHREEX XTHREEX XTHREEX XTHREEX XTHREEX "}
        ]"""

        val listType = object : TypeToken<List<HistoryInfo>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }
}