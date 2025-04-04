package com.samaxz.voxsplitapp.ui.detail

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.samaxz.voxsplitapp.databinding.DialogErrorBinding
import com.samaxz.voxsplitapp.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val detailViewModel by viewModels<DetailViewModel>()
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailFragmentArgs by navArgs()
    private var duration: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initUIState()
        initUiListeners()
    }

    private fun initUiListeners() {
        binding.playButton.setOnClickListener {
            // play
            detailViewModel.playAudio()
        }

        binding.pauseButton.setOnClickListener {
            // pause
            detailViewModel.pauseAudio()
        }

        val uriString = args.file
        val language = args.language
        val speakers = args.speakers

        val fileUri: Uri = Uri.parse(uriString)

        binding.btnSendRequest.setOnClickListener {
            detailViewModel.getResult(fileUri, speakers, language, requireContext().contentResolver)
        }
    }

    private fun initUIState() {
        val uriString = args.file
        val speakers = args.speakers

        val fileUri: Uri = Uri.parse(uriString)

        binding.tvSpeakers.text = "Speakers: $speakers"

        try {
            // Upload File
            detailViewModel.setAudioFile(fileUri, requireContext().contentResolver)
            //Init Media Player
            detailViewModel.initializeAudio { showDialog() }

            //Update components whit File
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailViewModel.audioFile.collect { audioFile ->
                        if (audioFile == null) {
                            binding.playButton.isEnabled = false
                            binding.pauseButton.isEnabled = false
                            binding.seekBar.isEnabled = false
                        }
                    }
                }
            }
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailViewModel.progress.collect { progress ->
                        binding.seekBar.progress = progress
                    }
                }
            }
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailViewModel.mediaPlayer.collect { mediaPlayer ->
                        if (mediaPlayer != null) {

                            duration = mediaPlayer.duration
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
                    detailViewModel.allCool.collect { cool ->
                        if (!cool) {
                            binding.playButton.isEnabled = false
                            binding.pauseButton.isEnabled = false
                            binding.seekBar.isEnabled = false
                            //Show Dialog
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
                            Log.i("SUPERSAMA", result.toString())
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

    override fun onDestroyView() {
        super.onDestroyView()
        detailViewModel.cleanData()
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        val bindingDialog = DialogErrorBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(bindingDialog.root)

        val btnContinue: Button = bindingDialog.btnContinue
        btnContinue.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}