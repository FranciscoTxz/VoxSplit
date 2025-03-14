package com.samaxz.voxsplitapp.domain.usecase

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import javax.inject.Inject

class GetAudioNameUseCase @Inject constructor() {
    operator fun invoke(uri: Uri, contentResolver: ContentResolver): String {
        var name = "???"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
}