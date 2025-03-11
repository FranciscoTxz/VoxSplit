package com.samaxz.voxsplitapp.view

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.samaxz.voxsplitapp.databinding.FragmentHomeBinding
import java.io.File
import java.io.FileOutputStream

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var file: File
    private lateinit var uriX: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        binding.rlUploadFile.setOnClickListener {
            selectAudio()
        }
        binding.btnChangeFile.setOnClickListener {
            selectAudio()
        }
        binding.btnProcess.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAudioFragment(file=uriX.toString()))
        }
        return binding.root
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
                    uriX = uri

                    binding.rlUploadFile.visibility = View.GONE
                    binding.tvFileName.text = fileName
                    binding.tvFileSize.text = "%.2f MB".format(fileSize)
                    binding.tvFileDuration.text = "%.2f S".format(duration.toDouble())
                    binding.cvUploaded.visibility = View.VISIBLE
                    binding.btnProcess.visibility = View.VISIBLE
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
}