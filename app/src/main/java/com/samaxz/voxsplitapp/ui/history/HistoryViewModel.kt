package com.samaxz.voxsplitapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import com.samaxz.voxsplitapp.domain.usecase.DeleteHistoryUseCase
import com.samaxz.voxsplitapp.domain.usecase.GetHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val deleteHistoryUseCase: DeleteHistoryUseCase
) :
    ViewModel() {

    private var _history = MutableStateFlow<List<HistoryInfo>>(emptyList())
    val history: StateFlow<List<HistoryInfo>> = _history

    private var _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            _isLoading.value = true
            val result = getHistoryUseCase()
            _history.value = result
            _isLoading.value = false
        }
    }

    fun updateList() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = getHistoryUseCase()
            _history.value = result
            _isLoading.value = false
        }
    }

    fun deleteHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            deleteHistoryUseCase()
            val result = getHistoryUseCase()
            _history.value = result
            _isLoading.value = false
        }
    }
}