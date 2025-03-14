package com.samaxz.voxsplitapp.domain.usecase

import android.content.ContentResolver
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import com.samaxz.voxsplitapp.domain.model.AudioMetadataModel
import javax.inject.Inject

class GetAudioMetadataUseCase @Inject constructor() {
    operator fun invoke(uri: Uri, contentResolver: ContentResolver): AudioMetadataModel {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(contentResolver.openFileDescriptor(uri, "r")?.fileDescriptor)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
            val size = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst() && sizeIndex != -1) cursor.getLong(sizeIndex) else 0L
            } ?: 0L
            retriever.release()
            AudioMetadataModel(duration, size)
        } catch (e: Exception) {
            retriever.release()
            AudioMetadataModel(0L, 0L)
        }
    }
}