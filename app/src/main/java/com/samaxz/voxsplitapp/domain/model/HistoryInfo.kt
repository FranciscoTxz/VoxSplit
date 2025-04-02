package com.samaxz.voxsplitapp.domain.model

import com.samaxz.voxsplitapp.R
import kotlinx.coroutines.internal.OpDescriptor

sealed class HistoryInfo(val name: Int, val description: Int) {
    data object One : HistoryInfo(R.string.one, R.string.one_desc)
    data object Two : HistoryInfo(R.string.two, R.string.two_desc)
    data object Three : HistoryInfo(R.string.three, R.string.three_desc)
}