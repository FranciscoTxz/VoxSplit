package com.samaxz.voxsplitapp.ui.detailhistory

import android.content.ContentResolver
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samaxz.voxsplitapp.domain.model.AudioFileModel
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import com.samaxz.voxsplitapp.domain.usecase.ConvertUriToFileUseCase
import com.samaxz.voxsplitapp.domain.usecase.GetAudioMetadataUseCase
import com.samaxz.voxsplitapp.domain.usecase.GetAudioNameUseCase
import com.samaxz.voxsplitapp.domain.usecase.GetOneHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailHistoryViewModel @Inject constructor(
    private val getAudioMetadataUseCase: GetAudioMetadataUseCase,
    private val convertUriToFileUseCase: ConvertUriToFileUseCase,
    private val getAudioNameUseCase: GetAudioNameUseCase,
    private val getOneHistoryUseCase: GetOneHistoryUseCase
) : ViewModel() {

    private var _audioFile = MutableStateFlow<AudioFileModel?>(null)
    val audioFile: StateFlow<AudioFileModel?> = _audioFile

    private var _allCool = MutableStateFlow<Boolean>(true)
    val allCool: StateFlow<Boolean> = _allCool

    private var _called = MutableStateFlow<Boolean>(false)

    private var _progress = MutableStateFlow<Int>(0)
    val progress: StateFlow<Int> = _progress

    private var _remainTime = MutableStateFlow<Int>(0)
    val remainTime: StateFlow<Int> = _remainTime

    private var _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    val mediaPlayer: StateFlow<MediaPlayer?> = _mediaPlayer

    private var _historyResult = MutableStateFlow<HistoryInfo?>(null)
    val historyResult: StateFlow<HistoryInfo?> = _historyResult

    private var _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getHistory(id: Int) {
        if (!_called.value) {
            _isLoading.value = true
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    _historyResult.value = getOneHistoryUseCase(id)
                }
                _called.value = true
                _isLoading.value = false
            }
        }
    }

    fun cleanData() {
        _allCool.value = true
        _audioFile.value = null
        _progress.value = 0
        _remainTime.value = 0
        _mediaPlayer.value?.release()
        _mediaPlayer.value = null
    }

    fun setAudioFile(uri: Uri, contentResolver: ContentResolver) {
        _mediaPlayer.value?.release()
        viewModelScope.launch {
            try {
                val file = convertUriToFileUseCase(uri, contentResolver)
                val name = getAudioNameUseCase(uri, contentResolver)
                val metadata = getAudioMetadataUseCase(uri, contentResolver)
                _audioFile.value = AudioFileModel(file, name, metadata)
            } catch (e: Exception) {
                _allCool.value = false
                _audioFile.value = null
                //Show Dialog
                Log.i("SUPERSAMA", "the file doest exist")
            }
        }
    }

    fun goToTime(timeS: Double) {
        if (_allCool.value == true && _mediaPlayer.value != null) {
            _mediaPlayer.value!!.seekTo((timeS * 1000).toInt())
        }
    }

    fun initializeAudio() {
        try {
            if (_mediaPlayer.value == null) {
                _mediaPlayer.value = MediaPlayer().apply {
                    setDataSource(_audioFile.value?.file?.absolutePath)
                    prepare()
                }
            }
            _allCool.value = true

        } catch (e: Exception) {
            _allCool.value = false
            _mediaPlayer.value?.release()
        }
    }

    fun playAudio() {
        try {
            if (_mediaPlayer.value != null && _allCool.value) {
                _mediaPlayer.value?.start()
                updateSeekBar()
            }

        } catch (e: Exception) {
            _allCool.value = false
            _mediaPlayer.value?.release()
        }
    }

    fun pauseAudio() {
        if (_allCool.value && _mediaPlayer.value?.isPlaying == true) {
            _mediaPlayer.value?.pause()
        }
    }

    private fun updateSeekBar() {
        viewModelScope.launch {
            while (_mediaPlayer.value?.isPlaying == true) {
                _progress.value = _mediaPlayer.value?.currentPosition ?: 0
                _remainTime.value = (_mediaPlayer.value?.duration ?: 0) - _progress.value
                delay(500)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _mediaPlayer.value?.release()
    }
}