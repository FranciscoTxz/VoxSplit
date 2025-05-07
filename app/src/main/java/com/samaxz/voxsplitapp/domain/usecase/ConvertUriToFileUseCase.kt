package com.samaxz.voxsplitapp.domain.usecase

import android.content.ContentResolver
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ConvertUriToFileUseCase @Inject constructor() {
    operator fun invoke(uri: Uri, contentResolver: ContentResolver): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File.createTempFile("temp_audio", ".wav")
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}