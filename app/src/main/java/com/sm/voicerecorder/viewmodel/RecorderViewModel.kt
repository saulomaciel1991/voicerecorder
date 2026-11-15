package com.sm.voicerecorder.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sm.voicerecorder.audio.AudioPlayer
import com.sm.voicerecorder.audio.AudioRecorder
import com.sm.voicerecorder.model.RecordingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecorderViewModel(application: Application) : AndroidViewModel(application) {

    private val recorder = AudioRecorder(application)
    private val player = AudioPlayer()

    private val recordingsDir: File by lazy {
        val dir = application.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)
            ?: File(application.filesDir, "recordings")
        if (!dir.exists()) dir.mkdirs()
        dir
    }

    private val _recordings = MutableStateFlow<List<RecordingItem>>(emptyList())
    val recordings: StateFlow<List<RecordingItem>> = _recordings.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDurationSeconds = MutableStateFlow(0L)
    val recordingDurationSeconds: StateFlow<Long> = _recordingDurationSeconds.asStateFlow()

    private val _playingFile = MutableStateFlow<File?>(null)
    val playingFile: StateFlow<File?> = _playingFile.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackProgressMs = MutableStateFlow(0)
    val playbackProgressMs: StateFlow<Int> = _playbackProgressMs.asStateFlow()

    private val _playbackDurationMs = MutableStateFlow(0)
    val playbackDurationMs: StateFlow<Int> = _playbackDurationMs.asStateFlow()

    private var recordingTimerJob: Job? = null
    private var playbackProgressJob: Job? = null

    init {
        loadRecordings()
    }

    fun loadRecordings() {
        viewModelScope.launch(Dispatchers.IO) {
            val files = recordingsDir.listFiles { file ->
                file.isFile && (file.extension.equals("m4a", ignoreCase = true) || file.extension.equals("mp3", ignoreCase = true))
            }?.sortedByDescending { it.lastModified() } ?: emptyList()

            val items = files.map { file ->
                val duration = getFileAudioDuration(file)
                RecordingItem(
                    file = file,
                    name = file.nameWithoutExtension,
                    durationMs = duration,
                    sizeBytes = file.length(),
                    lastModified = file.lastModified()
                )
            }

            _recordings.value = items
        }
    }

    private fun getFileAudioDuration(file: File): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        } catch (_: Exception) {
            0L
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {}
        }
    }

    fun startRecording(): Boolean {
        // Para qualquer playback em andamento
        stopPlayback()

        val timestamp = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(Date())
        val newFile = File(recordingsDir, "AudioMagico_$timestamp.m4a")

        val success = recorder.start(newFile)
        if (success) {
            _isRecording.value = true
            _recordingDurationSeconds.value = 0L

            recordingTimerJob?.cancel()
            recordingTimerJob = viewModelScope.launch {
                while (_isRecording.value) {
                    delay(1000)
                    _recordingDurationSeconds.value += 1
                }
            }
        }
        return success
    }

    fun stopRecording() {
        recordingTimerJob?.cancel()
        recordingTimerJob = null
        recorder.stop()
        _isRecording.value = false
        _recordingDurationSeconds.value = 0L
        loadRecordings()
    }

    fun togglePlayPause(recording: RecordingItem) {
        if (_playingFile.value?.absolutePath == recording.file.absolutePath) {
            if (_isPlaying.value) {
                player.pause()
                _isPlaying.value = false
                playbackProgressJob?.cancel()
            } else {
                player.resume()
                _isPlaying.value = true
                startProgressTracker()
            }
        } else {
            // Tocar novo arquivo
            val success = player.play(recording.file) {
                // Ao terminar
                _isPlaying.value = false
                _playbackProgressMs.value = 0
                playbackProgressJob?.cancel()
            }
            if (success) {
                _playingFile.value = recording.file
                _isPlaying.value = true
                _playbackDurationMs.value = player.duration
                startProgressTracker()
            }
        }
    }

    fun seekTo(positionMs: Int) {
        player.seekTo(positionMs)
        _playbackProgressMs.value = positionMs
    }

    private fun startProgressTracker() {
        playbackProgressJob?.cancel()
        playbackProgressJob = viewModelScope.launch {
            while (_isPlaying.value) {
                _playbackProgressMs.value = player.currentPosition
                _playbackDurationMs.value = player.duration
                delay(100)
            }
        }
    }

    fun stopPlayback() {
        playbackProgressJob?.cancel()
        playbackProgressJob = null
        player.release()
        _playingFile.value = null
        _isPlaying.value = false
        _playbackProgressMs.value = 0
        _playbackDurationMs.value = 0
    }

    fun deleteRecording(recording: RecordingItem) {
        if (_playingFile.value?.absolutePath == recording.file.absolutePath) {
            stopPlayback()
        }
        if (recording.file.exists()) {
            recording.file.delete()
        }
        loadRecordings()
    }

    fun shareRecording(context: Context, recording: RecordingItem) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                recording.file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Compartilhar gravação")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        recorder.release()
        player.release()
        recordingTimerJob?.cancel()
        playbackProgressJob?.cancel()
    }
}
