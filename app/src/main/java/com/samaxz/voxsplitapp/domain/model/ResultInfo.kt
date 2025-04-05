package com.samaxz.voxsplitapp.domain.model

import com.samaxz.voxsplitapp.data.model.ResultModel
import com.samaxz.voxsplitapp.data.model.Segment
import com.samaxz.voxsplitapp.data.model.Word

data class ResultInfo(
    val text: String,
    val segments: List<SegmentD>,
    val language: String
)

data class SegmentD(
    val id: Int,
    val start: Double,
    val end: Double,
    val text: String,
    val words: List<WordD>,
    val prob: List<String>,
    val overlapp: String
)

data class WordD(
    val word: String,
    val start: Double,
    val end: Double,
    val speaker: String,
    val overlapp: Boolean? = null
)

fun ResultModel.toDomain() = ResultInfo(
    text = this.text,
    segments = this.segments.map { it.toDomain() },
    language = this.language
)

fun Segment.toDomain() = SegmentD(
    id = this.id,
    start = this.start,
    end = this.end,
    text = this.text,
    words = this.words.map { it.toDomain() },
    prob = this.prob,
    overlapp = this.overlapp
)

fun Word.toDomain() = WordD(
    word = this.word,
    start = this.start,
    end = this.end,
    speaker = this.speaker,
    overlapp = this.overlapp
)