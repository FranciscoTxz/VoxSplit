package com.samaxz.voxsplitapp.domain.model

import java.io.File

data class AudioFileModel (
    val file : File,
    val name : String,
    val metadataModel: AudioMetadataModel
)
data class AudioMetadataModel (
    val duration: Long,
    val size: Long

)