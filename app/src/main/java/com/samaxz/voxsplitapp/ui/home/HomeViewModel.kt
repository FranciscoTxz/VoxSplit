package com.samaxz.voxsplitapp.ui.home

import android.content.ContentResolver
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samaxz.voxsplitapp.domain.model.AudioFileModel
import com.samaxz.voxsplitapp.domain.usecase.ConvertUriToFileUseCase
import com.samaxz.voxsplitapp.domain.usecase.GetAudioMetadataUseCase
import com.samaxz.voxsplitapp.domain.usecase.GetAudioNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAudioMetadataUseCase: GetAudioMetadataUseCase,
    private val convertUriToFileUseCase: ConvertUriToFileUseCase,
    private val getAudioNameUseCase: GetAudioNameUseCase,
) : ViewModel() {

    private var _audioFile = MutableStateFlow<AudioFileModel?>(null)
    val audioFile: StateFlow<AudioFileModel?> = _audioFile

    private var _allCool = MutableStateFlow<Boolean>(true)
    val allCool: StateFlow<Boolean> = _allCool

    private var _progress = MutableStateFlow<Int>(0)
    val progress: StateFlow<Int> = _progress

    private var _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    val mediaPlayer: StateFlow<MediaPlayer?> = _mediaPlayer

    fun cleanData() {
        _allCool.value = true
        _audioFile.value = null
        _progress.value = 0
        _mediaPlayer.value = null
    }

    fun setAudioFile(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            val file = convertUriToFileUseCase(uri, contentResolver)
            val name = getAudioNameUseCase(uri, contentResolver)
            val metadata = getAudioMetadataUseCase(uri, contentResolver)
            _audioFile.value = AudioFileModel(file, name, metadata)
        }
    }

    fun playAudio() {
        try {
            if (_mediaPlayer.value == null) {
                _mediaPlayer.value = MediaPlayer().apply {
                    setDataSource(_audioFile.value?.file?.absolutePath)
                    prepare()
                }
            }
            _allCool.value = true
            Log.i("SUPERSAMA", "CHIDO")
            _mediaPlayer.value?.start()

            updateSeekBar()

        } catch(e : Exception){
            Log.i("SUPERSAMA", "ERROR ESPERADO")
            _allCool.value = false
        }
    }

    fun pauseAudio() {
        if (_allCool.value){ _mediaPlayer.value?.pause() }
    }

    private fun updateSeekBar() {
        viewModelScope.launch {
            while (_mediaPlayer.value?.isPlaying == true) {
                _progress.value = _mediaPlayer.value?.currentPosition ?: 0
                delay(500)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _mediaPlayer.value?.release()
    }
}