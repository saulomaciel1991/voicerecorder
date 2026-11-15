package com.sm.voicerecorder.audio

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import java.io.File

class AudioPlayer {
    private var player: MediaPlayer? = null
    var currentPlayingFile: File? = null
        private set

    val isPlaying: Boolean
        get() = player?.isPlaying == true

    val currentPosition: Int
        get() = try {
            player?.currentPosition ?: 0
        } catch (_: Exception) {
            0
        }

    val duration: Int
        get() = try {
            player?.duration ?: 0
        } catch (_: Exception) {
            0
        }

    fun play(file: File, onCompletion: () -> Unit): Boolean {
        return try {
            release()
            player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setOnCompletionListener {
                    onCompletion()
                }
                start()
            }
            currentPlayingFile = file
            true
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Erro ao reproduzir arquivo ${file.name}", e)
            release()
            false
        }
    }

    fun pause() {
        try {
            if (player?.isPlaying == true) {
                player?.pause()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Erro ao pausar", e)
        }
    }

    fun resume() {
        try {
            if (player?.isPlaying == false) {
                player?.start()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Erro ao retomar reprodução", e)
        }
    }

    fun seekTo(positionMs: Int) {
        try {
            player?.seekTo(positionMs)
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Erro ao avançar/retroceder", e)
        }
    }

    fun release() {
        try {
            player?.stop()
            player?.reset()
            player?.release()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Erro ao liberar MediaPlayer", e)
        } finally {
            player = null
            currentPlayingFile = null
        }
    }
}
