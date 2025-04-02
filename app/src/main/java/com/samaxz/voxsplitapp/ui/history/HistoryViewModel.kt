package com.samaxz.voxsplitapp.ui.history

import androidx.lifecycle.ViewModel
import com.samaxz.voxsplitapp.data.providers.HistoryProvider
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(horoscopeProvider: HistoryProvider) :
    ViewModel() {

    //Its a flow than can only by called by the horoscope state flow that is not mutable
    private var _history = MutableStateFlow<List<HistoryInfo>>(emptyList())
    val history: StateFlow<List<HistoryInfo>> = _history

    init {
        _history.value = horoscopeProvider.getHistory()
    }
}