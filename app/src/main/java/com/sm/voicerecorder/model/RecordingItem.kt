package com.sm.voicerecorder.model

import java.io.File

data class RecordingItem(
    val file: File,
    val name: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val lastModified: Long
)
