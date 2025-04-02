package com.samaxz.voxsplitapp.data.model

import com.google.gson.annotations.SerializedName

data class HistoryModel(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)
