package com.sm.voicerecorder.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    var isRecording: Boolean = false
        private set

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    fun start(outputFile: File): Boolean {
        return try {
            stop() // Para qualquer gravação anterior caso exista

            recorder = createRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(FileOutputStream(outputFile).fd)

                prepare()
                start()
            }
            isRecording = true
            true
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Erro ao iniciar gravação", e)
            release()
            false
        }
    }

    fun stop(): Boolean {
        if (!isRecording) return false
        return try {
            recorder?.apply {
                stop()
                reset()
                release()
            }
            recorder = null
            isRecording = false
            true
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Erro ao parar gravação", e)
            release()
            false
        }
    }

    fun release() {
        try {
            recorder?.reset()
            recorder?.release()
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Erro ao liberar recursos do gravador", e)
        } finally {
            recorder = null
            isRecording = false
        }
    }
}
