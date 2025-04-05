package com.samaxz.voxsplitapp.ui.detail

import android.content.ContentResolver
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samaxz.voxsplitapp.domain.model.AudioFileModel
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import com.samaxz.voxsplitapp.domain.model.ResultInfo
import com.samaxz.voxsplitapp.domain.usecase.ConvertUriToFileUseCase
import com.samaxz.voxsplitapp.domain.usecase.GetAudioMetadataUseCase
import com.samaxz.voxsplitapp.domain.usecase.GetAudioNameUseCase
import com.samaxz.voxsplitapp.domain.usecase.PostNewHistoryUseCase
import com.samaxz.voxsplitapp.domain.usecase.PostResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val postResultUseCase: PostResultUseCase,
    private val getAudioMetadataUseCase: GetAudioMetadataUseCase,
    private val convertUriToFileUseCase: ConvertUriToFileUseCase,
    private val getAudioNameUseCase: GetAudioNameUseCase,
    private val postNewHistory: PostNewHistoryUseCase
) : ViewModel() {

    private var _audioFile = MutableStateFlow<AudioFileModel?>(null)
    val audioFile: StateFlow<AudioFileModel?> = _audioFile

    private var _allCool = MutableStateFlow<Boolean>(true)
    val allCool: StateFlow<Boolean> = _allCool

    private var _saved = MutableStateFlow<Boolean>(false)
    val saved: StateFlow<Boolean> = _saved

    private var _called = MutableStateFlow<Boolean>(false)
    val called: StateFlow<Boolean> = _called

    private var _progress = MutableStateFlow<Int>(0)
    val progress: StateFlow<Int> = _progress

    private var _remainTime = MutableStateFlow<Int>(0)
    val remainTime: StateFlow<Int> = _remainTime

    private var _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    val mediaPlayer: StateFlow<MediaPlayer?> = _mediaPlayer

    private var _result = MutableStateFlow<ResultInfo?>(null)
    val result: StateFlow<ResultInfo?> = _result


    fun getResult(uri: Uri, speakers: Int, language: String, contentResolver: ContentResolver) {
        if (!_called.value) {
            viewModelScope.launch {
                val data = withContext(Dispatchers.IO) {
                    _result.value = postResultUseCase(
                        speakers = speakers,
                        language = language,
                        file = convertUriToFileUseCase(uri, contentResolver)
                    )
                }
            }
            _called.value = true
        }
    }

    fun uploadToRoom(historyInfo: HistoryInfo) {
        if (!_saved.value) {
            viewModelScope.launch {
                val data = withContext(Dispatchers.IO) {
                    postNewHistory(historyInfo)
                }
            }
            _saved.value = true
        }
    }

    fun cleanData() {
        _allCool.value = true
        _audioFile.value = null
        _progress.value = 0
        _remainTime.value = 0
        _mediaPlayer.value = null
    }

    fun setAudioFile(uri: Uri, contentResolver: ContentResolver) {
        _mediaPlayer.value?.release()
        viewModelScope.launch {
            val file = convertUriToFileUseCase(uri, contentResolver)
            val name = getAudioNameUseCase(uri, contentResolver)
            val metadata = getAudioMetadataUseCase(uri, contentResolver)
            _audioFile.value = AudioFileModel(file, name, metadata)
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
            if (_mediaPlayer.value != null) {
                _allCool.value = true
                _mediaPlayer.value?.start()
                updateSeekBar()
            }

        } catch (e: Exception) {
            _allCool.value = false
            _mediaPlayer.value?.release()
        }
    }

    fun pauseAudio() {
        if (_allCool.value) {
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