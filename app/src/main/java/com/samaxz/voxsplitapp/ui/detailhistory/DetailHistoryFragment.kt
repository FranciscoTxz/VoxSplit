package com.samaxz.voxsplitapp.ui.detailhistory

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.samaxz.voxsplitapp.databinding.FragmentDetailHistoryBinding
import com.samaxz.voxsplitapp.ui.detailhistory.adapter.DetailHistoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.getValue

@AndroidEntryPoint
class DetailHistoryFragment : Fragment() {

    private val detailHistoryViewModel by viewModels<DetailHistoryViewModel>()
    private lateinit var detailHistoryAdapter: DetailHistoryAdapter

    private var _binding: FragmentDetailHistoryBinding? = null
    private val binding get() = _binding!!

    private val args: DetailHistoryFragmentArgs by navArgs()
    private var duration: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }

    private fun initUI() {
        initUIState()
        initUIListeners()
        initList()
    }

    private fun initList() {
        // RV
        detailHistoryAdapter = DetailHistoryAdapter(
            onItemSelected = { detailHistoryViewModel.goToTime(it.start) }
        )
        binding.rvResult.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = detailHistoryAdapter
        }
    }

    private fun initUIListeners() {
        binding.playButton.setOnClickListener {
            // play
            detailHistoryViewModel.playAudio()
            binding.playButton.isVisible = false
            binding.pauseButton.isVisible = true
        }

        binding.pauseButton.setOnClickListener {
            // pause
            detailHistoryViewModel.pauseAudio()
            binding.pauseButton.isVisible = false
            binding.playButton.isVisible = true
        }
    }

    private fun initUIState() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailHistoryViewModel.historyResult.collect { historyResult ->
                    if (historyResult != null) {
                        binding.tvFileNameDetail.text = historyResult.name
                        binding.tvFileSizeDetail.text = historyResult.size
                        binding.tvFileDurationDetail.text = historyResult.time

                        //Update UI
                        binding.cvMediaPLayer.isVisible = true
                        binding.svResult.isVisible = true
                        binding.tvTranscription.text = historyResult.result.text
                        binding.tvLanguage.text = historyResult.language.uppercase()
                        binding.tvSpeakers.text = historyResult.speakers.uppercase()

                        val fileUri: Uri = Uri.parse(historyResult.uri)
                        detailHistoryViewModel.setAudioFile(
                            fileUri,
                            requireContext().contentResolver
                        )

                        //Update RV
                        detailHistoryAdapter.updateList(historyResult.result.segments)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailHistoryViewModel.audioFile.collect { audioFile ->
                    if (audioFile == null) {
                        binding.playButton.isEnabled = false
                        binding.pauseButton.isEnabled = false
                        binding.seekBar.isEnabled = false
                    } else {
                        detailHistoryViewModel.initializeAudio()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailHistoryViewModel.progress.collect { progress ->
                    binding.seekBar.progress = progress
                    binding.tvCurrentTime.text = formatTime(progress)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailHistoryViewModel.remainTime.collect { remainTime ->
                    binding.tvRemainTime.text = formatTime(remainTime)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailHistoryViewModel.mediaPlayer.collect { mediaPlayer ->
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
                detailHistoryViewModel.isLoading.collect { loading ->
                    binding.pbLoadingDetail.isVisible = loading
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailHistoryViewModel.allCool.collect { cool ->
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
    }

    private fun formatTime(millis: Int): CharSequence {
        val minutes = millis / 1000 / 60
        val seconds = (millis / 1000) % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        detailHistoryViewModel.pauseAudio()
        detailHistoryViewModel.cleanData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Call ROOM
        val id = args.id
        detailHistoryViewModel.getHistory(id)
        _binding = FragmentDetailHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}