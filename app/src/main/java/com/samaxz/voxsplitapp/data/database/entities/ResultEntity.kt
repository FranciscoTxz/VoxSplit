package com.samaxz.voxsplitapp.data.database.entities

import com.samaxz.voxsplitapp.domain.model.ResultInfo
import com.samaxz.voxsplitapp.domain.model.SegmentD
import com.samaxz.voxsplitapp.domain.model.WordD

data class ResultEntity(
    val text: String,
    val segments: List<SegmentE>,
    val language: String
)

data class SegmentE(
    val id: Int,
    val start: Double,
    val end: Double,
    val text: String,
    val words: List<WordE>,
    val prob: List<String>,
    val overlapp: String
)

data class WordE(
    val word: String,
    val start: Double,
    val end: Double,
    val speaker: String,
    val overlapp: Boolean? = null
)

fun ResultInfo.toDatabase() = ResultEntity(
    text = this.text,
    segments = this.segments.map { it.toDatabase() },
    language = this.language
)

fun SegmentD.toDatabase() = SegmentE(
    id = this.id,
    start = this.start,
    end = this.end,
    text = this.text,
    words = this.words.map { it.toDatabase() },
    prob = this.prob,
    overlapp = this.overlapp
)

fun WordD.toDatabase() = WordE(
    word = this.word,
    start = this.start,
    end = this.end,
    speaker = this.speaker,
    overlapp = this.overlapp
)