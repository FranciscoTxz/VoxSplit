package com.samaxz.voxsplitapp.ui.detail

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.navigation.fragment.navArgs
import com.samaxz.voxsplitapp.R
import com.samaxz.voxsplitapp.databinding.FragmentDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var seekBar: SeekBar
    private var updateJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uriString = args.file

        val playButton = binding.playButton
        val pauseButton = binding.pauseButton
        seekBar = binding.seekBar

        val fileUri: Uri = Uri.parse(uriString)

        try {
            updateJob?.cancel()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(requireContext(), fileUri)
                prepare()
            }

            seekBar.max = mediaPlayer?.duration ?: 0

            startSeekBarUpdate()

            playButton.setOnClickListener {
                mediaPlayer?.start()
            }

            pauseButton.setOnClickListener {
                mediaPlayer?.pause()
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        } catch (e: Exception) {
            e.printStackTrace()
            playButton.isEnabled = false
            pauseButton.isEnabled = false
            seekBar.isEnabled = false
        }

    }

    private fun startSeekBarUpdate() {
        updateJob?.cancel()

        updateJob = CoroutineScope(Dispatchers.Main).launch {
            while (mediaPlayer?.isPlaying == true) {
                seekBar.progress = mediaPlayer?.currentPosition ?: 0
                delay(1000)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        updateJob?.cancel()
    }
}