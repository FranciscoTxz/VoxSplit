package com.samaxz.voxsplitapp.data.model

import com.google.gson.annotations.SerializedName

data class ResultModel(
    @SerializedName("text") val text: String,
    @SerializedName("segments") val segments: List<Segment>,
    @SerializedName("language") val language: String
)

data class Segment(
    @SerializedName("id") val id: Int,
    @SerializedName("start") val start: Double,
    @SerializedName("end") val end: Double,
    @SerializedName("text") val text: String,
    @SerializedName("words") val words: List<Word>,
    @SerializedName("prob") val prob: List<String>,
    @SerializedName("overlapp") val overlapp: String
)

data class Word(
    @SerializedName("word") val word: String,
    @SerializedName("start") val start: Double,
    @SerializedName("end") val end: Double,
    @SerializedName("speaker") val speaker: String,
    @SerializedName("overlapp") val overlapp: Boolean? = null
)