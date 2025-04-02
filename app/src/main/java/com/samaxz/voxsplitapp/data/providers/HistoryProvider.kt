package com.samaxz.voxsplitapp.data.providers

import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import javax.inject.Inject

class HistoryProvider @Inject constructor() {
    fun getHistory(): List<HistoryInfo> {
        return listOf(
            HistoryInfo.One,
            HistoryInfo.Two,
            HistoryInfo.Three
        )
    }
}