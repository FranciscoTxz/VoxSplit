package com.samaxz.voxsplitapp.ui.detail

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.samaxz.voxsplitapp.databinding.FragmentDetailBinding
import com.samaxz.voxsplitapp.domain.model.HistoryInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val detailViewModel by viewModels<DetailViewModel>()
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailFragmentArgs by navArgs()
    private var duration: Int? = null

    private lateinit var fileName: String
    private lateinit var fileSize: String
    private lateinit var fileDuration: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initTV()
        initUIState()
        initUiListeners()
    }

    private fun initTV() {
        val language = args.language
        val speakers = args.speakers
        binding.tvSpeakers.text = "S: $speakers"
        binding.tvLanguage.text = "L: ${language.uppercase()}"
    }

    private fun initUiListeners() {
        binding.playButton.setOnClickListener {
            // play
            detailViewModel.playAudio()
            binding.playButton.isVisible = false
            binding.pauseButton.isVisible = true
        }

        binding.pauseButton.setOnClickListener {
            // pause
            detailViewModel.pauseAudio()
            binding.pauseButton.isVisible = false
            binding.playButton.isVisible = true
        }
    }

    private fun initUIState() {
        val uriString = args.file
        val speakers = args.speakers
        val language = args.language
        val fileUri: Uri = Uri.parse(uriString)

        binding.tvSpeakers.text = "S: $speakers"

        try {
            // Upload File
            detailViewModel.setAudioFile(fileUri, requireContext().contentResolver)
            //Init Media Player
            detailViewModel.initializeAudio()

            //Update components whit File
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailViewModel.audioFile.collect { audioFile ->
                        if (audioFile == null) {
                            binding.playButton.isEnabled = false
                            binding.pauseButton.isEnabled = false
                            binding.seekBar.isEnabled = false
                        } else {
                            audioFile.let {
                                fileName = audioFile.name
                                fileDuration = String.format(
                                    Locale.US,
                                    "%.1f S",
                                    (audioFile.metadataModel.duration.toFloat() / 1000)
                                )
                                fileSize = String.format(
                                    Locale.US,
                                    "%.1f MB",
                                    (audioFile.metadataModel.size.toFloat() / (1024.0 * 1024.0))
                                )
                                binding.tvFileNameDetail.text = fileName
                                binding.tvFileSizeDetail.text = fileSize
                                binding.tvFileDurationDetail.text = fileDuration
                            }
                        }
                    }
                }
            }
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailViewModel.progress.collect { progress ->
                        binding.seekBar.progress = progress
                        binding.tvCurrentTime.text = formatTime(progress)
                    }
                }
            }
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailViewModel.remainTime.collect { remainTime ->
                        binding.tvRemainTime.text = formatTime(remainTime)
                    }
                }
            }
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailViewModel.mediaPlayer.collect { mediaPlayer ->
                        if (mediaPlayer != null) {

                            duration = mediaPlayer.duration
                            binding.seekBar.max = mediaPlayer.duration

                            mediaPlayer.setOnCompletionListener {
                                binding.pauseButton.isVisible = false
                                binding.playButton.isVisible = true
                            }

                            binding.seekBar.setOnSeekBarChangeListener(object :
                                SeekBar.OnSeekBarChangeListener {
                                override fun onProgressChanged(
                                    seekBar: SeekBar?,
                                    progress: Int,
                                    fromUser: Boolean
                                ) {
                                    if (fromUser) {
                                        mediaPlayer.seekTo(progress)
                                        binding.tvCurrentTime.text = formatTime(progress)
                                        binding.tvRemainTime.text =
                                            formatTime(mediaPlayer.duration - progress)
                                    }
                                }

                                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                            })
                        }
                    }
                }
            }
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailViewModel.allCool.collect { cool ->
                        if (!cool) {
                            binding.playButton.isEnabled = false
                            binding.pauseButton.isEnabled = false
                            binding.seekBar.isEnabled = false
                        } else {
                            binding.playButton.isEnabled = true
                            binding.pauseButton.isEnabled = true
                            binding.seekBar.isEnabled = true
                        }
                    }
                }
            }
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailViewModel.result.collect { result ->
                        if (result != null) {
                            // Put Loadings
                            Log.i("SUPERSAMA", result.toString())
                            saveInRoom(
                                name = fileName,
                                uri = uriString,
                                speakers = "S: $speakers",
                                language = "L: $language",
                                size = fileSize,
                                time = fileDuration,
                                description = result.text,
                            )
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            binding.playButton.isEnabled = false
            binding.pauseButton.isEnabled = false
            binding.seekBar.isEnabled = false
        }
    }

    private fun saveInRoom(
        name: String,
        uri: String,
        speakers: String,
        language: String,
        size: String,
        time: String,
        description: String
    ) {
        // Save in ROOM
        detailViewModel.uploadToRoom(
            HistoryInfo(
                id = null,
                name = name,
                uri = uri,
                speakers = speakers,
                language = language,
                size = size,
                time = time,
                description = description,
            )
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        detailViewModel.pauseAudio()
        detailViewModel.cleanData()
    }

    private fun formatTime(millis: Int): CharSequence {
        val minutes = millis / 1000 / 60
        val seconds = (millis / 1000) % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Request
        val uriString = args.file
        val language = args.language
        val speakers = args.speakers

        val fileUri: Uri = Uri.parse(uriString)

        detailViewModel.getResult(fileUri, speakers, language, requireContext().contentResolver)

        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}