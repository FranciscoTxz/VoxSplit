package com.samaxz.voxsplitapp.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.samaxz.voxsplitapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var uriX: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initUIState()
    }

    private fun initUI() {
        binding.rlUploadFile.setOnClickListener {
            selectAudio()
        }
        binding.btnChangeFile.setOnClickListener {
            homeViewModel.cleanData()
            selectAudio()
        }
        binding.btnProcess.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToAudioFragment(
                    file = uriX.toString()
                )
            )
        }
    }


    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.audioFile.collect { audioFile ->
                    if (audioFile != null) {
                        binding.rlUploadFile.visibility = View.GONE
                        binding.cvUploaded.visibility = View.VISIBLE
                        binding.btnProcess.visibility = View.VISIBLE
                    }
                    audioFile?.let {
                        binding.tvFileName.text = audioFile.name
                        binding.tvFileSize.text = String.format(
                            Locale.US,
                            "%.2f MB",
                            (audioFile.metadataModel.size.toFloat() / (1024.0 * 1024.0))
                        )
                        binding.tvFileDuration.text = String.format(
                            Locale.US,
                            "%.2f S",
                            (audioFile.metadataModel.duration.toFloat() / 1000)
                        )
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.progress.collect {
                    binding.seekBar.progress = it
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.mediaPlayer.collect { mediaPlayer ->
                    if (mediaPlayer != null) {
                        binding.seekBar.max = mediaPlayer.duration

                        binding.seekBar.setOnSeekBarChangeListener(object :
                            SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar?,
                                progress: Int,
                                fromUser: Boolean
                            ) {
                                if (fromUser) {
                                    mediaPlayer.seekTo(progress)
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
                homeViewModel.allCool.collect { cool ->
                    if (!cool) {
                        binding.btnPlay.isEnabled = false
                        binding.btnPause.isEnabled = false
                        binding.btnProcess.isEnabled = false
                    } else {
                        binding.btnPlay.isEnabled = true
                        binding.btnPause.isEnabled = true
                        binding.btnProcess.isEnabled = true
                    }
                }
            }
        }

        binding.btnPlay.setOnClickListener { homeViewModel.playAudio() }
        binding.btnPause.setOnClickListener { homeViewModel.pauseAudio() }
    }

    private fun selectAudio() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "audio/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        selectAudioLauncher.launch(intent)
    }

    private val selectAudioLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    homeViewModel.setAudioFile(uri, requireContext().contentResolver)
                    uriX = uri
                }
            }
        }
}