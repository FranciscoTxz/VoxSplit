package com.samaxz.voxsplitapp.ui.home

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.samaxz.voxsplitapp.databinding.DialogErrorBinding
import com.samaxz.voxsplitapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel by viewModels<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var uriX: Uri
    private var duration: Int? = null

    private lateinit var fileName: String
    private lateinit var fileSize: String
    private lateinit var fileDuration: String
    private var speakers: Int = 2
    private var language: String = "en"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initUiListeners()
        initUIState()
    }

    private fun initUiListeners() {
        binding.rlUploadFile.setOnClickListener {
            selectAudio()
        }
        binding.btnChangeFile.setOnClickListener {
            homeViewModel.pauseAudio()
            homeViewModel.cleanData()
            binding.btnPause.isVisible = false
            binding.btnPlay.isVisible = true
            selectAudio()
        }
        binding.btnProcess.setOnClickListener {
            homeViewModel.pauseAudio()
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToAudioFragment(
                    file = uriX.toString(),
                    language = language,
                    speakers = speakers
                )
            )
        }
        val items = listOf("1", "2", "3", "4")
        val adapter =
            ArrayAdapter(binding.spinnerSpeakers.context, R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(com.samaxz.voxsplitapp.R.layout.spinner_item)
        binding.spinnerSpeakers.adapter = adapter
        binding.spinnerSpeakers.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    speakers = (items[position]).toInt()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        val itemsLanguage = listOf("en", "es")
        val adapterLanguage =
            ArrayAdapter(
                binding.spinnerLanguage.context,
                R.layout.simple_spinner_item,
                itemsLanguage
            )
        adapterLanguage.setDropDownViewResource(com.samaxz.voxsplitapp.R.layout.spinner_item)
        binding.spinnerLanguage.adapter = adapterLanguage
        binding.spinnerLanguage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    language = (itemsLanguage[position])
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        binding.btnPlay.setOnClickListener {
            homeViewModel.playAudio { showDialog() }
            binding.btnPlay.isVisible = false
            binding.btnPause.isVisible = true
        }
        binding.btnPause.setOnClickListener {
            homeViewModel.pauseAudio()
            binding.btnPause.isVisible = false
            binding.btnPlay.isVisible = true
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
                        binding.tvHomeText.isVisible = false
                    }
                    audioFile?.let {
                        fileName = audioFile.name
                        fileDuration = String.format(
                            Locale.US,
                            "%.2f S",
                            (audioFile.metadataModel.duration.toFloat() / 1000)
                        )
                        fileSize = String.format(
                            Locale.US,
                            "%.2f MB",
                            (audioFile.metadataModel.size.toFloat() / (1024.0 * 1024.0))
                        )
                        binding.tvFileName.text = fileName
                        binding.tvFileSize.text = fileSize
                        binding.tvFileDuration.text = fileDuration
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.progress.collect { progress ->
                    binding.seekBar.progress = progress
                    binding.tvAudioStart.text = formatTime(progress)

                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.remainTime.collect { remainTime ->
                    binding.tvAudioEnd.text = formatTime(remainTime)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.mediaPlayer.collect { mediaPlayer ->
                    if (mediaPlayer != null) {

                        duration = mediaPlayer.duration
                        binding.seekBar.max = mediaPlayer.duration

                        mediaPlayer.setOnCompletionListener {
                            binding.btnPause.isVisible = false
                            binding.btnPlay.isVisible = true
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
                                    binding.tvAudioStart.text = formatTime(progress)
                                    binding.tvAudioEnd.text =
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
                homeViewModel.allCool.collect { cool ->
                    if (!cool) {
                        binding.btnPlay.isEnabled = false
                        binding.btnPause.isEnabled = false
                        binding.btnProcess.isEnabled = false
                        //Show Dialog
                    } else {
                        binding.btnPlay.isEnabled = true
                        binding.btnPause.isEnabled = true
                        binding.btnProcess.isEnabled = true
                    }
                }
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.pauseAudio()
        homeViewModel.cleanData()
    }

    private fun formatTime(millis: Int): CharSequence {
        val minutes = millis / 1000 / 60
        val seconds = (millis / 1000) % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
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
}