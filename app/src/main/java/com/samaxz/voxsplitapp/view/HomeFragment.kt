package com.samaxz.voxsplitapp.view

import android.R
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.samaxz.voxsplitapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var file: File
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var seekBar: SeekBar
    private var updateJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rlUploadFile.setOnClickListener {
            selectAudio()
        }
        binding.btnChangeFile.setOnClickListener {
            selectAudio()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        updateJob?.cancel()
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
                val selectedAudioUri: Uri? = result.data?.data
                selectedAudioUri?.let { uri ->
                    uploadAudio(uri, requireContext().contentResolver)
                    val fileName = getFileName(uri, requireContext().contentResolver)
                    val fileSize = getFileSize(
                        uri, requireContext().contentResolver
                    ) / (1024.0 * 1024.0)
                    val duration = getAudioDuration(
                        uri, requireContext().contentResolver
                    ) / 1000

                    binding.rlUploadFile.visibility = View.GONE
                    binding.tvHomeText.visibility = View.GONE
                    val items = listOf("1", "2", "3", "4", "?")
                    val adapter = ArrayAdapter(binding.spinnerSpeakers.context, R.layout.simple_spinner_item, items)
                    binding.spinnerSpeakers.adapter = adapter
                    binding.tvFileName.text = fileName
                    binding.tvFileSize.text = String.format(Locale.US, "%.2f MB", fileSize)
                    binding.tvFileDuration.text = String.format(Locale.US, "%.2f S", duration.toDouble())
                    binding.cvUploaded.visibility = View.VISIBLE
                    binding.btnProcess.visibility = View.VISIBLE
                    binding.btnProcess.setOnClickListener {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAudioFragment(file=uri.toString()))
                    }

                    binding.spinnerSpeakers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val selectedOption = items[position]
                            Toast.makeText(binding.spinnerSpeakers.context, "Seleccionaste: $selectedOption", Toast.LENGTH_SHORT).show()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }

                    updateMediaSeekBar(uri)
                }
            }
        }

    private fun uploadAudio(fileUri: Uri, contentResolver: ContentResolver) {
        uriToFile(fileUri, contentResolver)
        Log.i("FILEXZ", fileUri.toString())
    }

    private fun uriToFile(uri: Uri, contentResolver: ContentResolver) {
        val inputStream = contentResolver.openInputStream(uri)
        file = File.createTempFile("temp_audio", ".wav")
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun getFileName(uri: Uri, contentResolver: ContentResolver): String {
        var name = "???"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        Log.i("FILEXZ", name)
        return name
    }

    private fun getAudioDuration(uri: Uri, contentResolver: ContentResolver): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(contentResolver.openFileDescriptor(uri, "r")?.fileDescriptor)
            val durationStr =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            durationStr?.toLong() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    private fun getFileSize(uri: Uri, contentResolver: ContentResolver): Long {
        var size: Long = 0
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1 && cursor.moveToFirst()) {
                size = cursor.getLong(sizeIndex)
            }
        }
        return size
    }

    private fun updateMediaSeekBar(uri: Uri){
        val playButton = binding.btnPlay
        val pauseButton = binding.btnPause
        seekBar = binding.seekBar

        val fileUri: Uri = Uri.parse(uri.toString())

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

        val duration = mediaPlayer?.duration ?: 0

        updateJob = CoroutineScope(Dispatchers.Main).launch {
            while (mediaPlayer != null) {
                val currentPosition = mediaPlayer?.currentPosition ?: 0
                val remainingTime = duration - currentPosition

                binding.tvAudioStart.text = formatTime(currentPosition)
                binding.tvAudioEnd.text = formatTime(remainingTime)
                seekBar.progress = mediaPlayer?.currentPosition ?: 0
                delay(500)
            }
        }
    }

    private fun formatTime(millis: Int): CharSequence {
        val minutes = millis / 1000 / 60
        val seconds = (millis / 1000) % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}