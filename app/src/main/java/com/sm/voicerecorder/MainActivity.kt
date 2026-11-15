package com.sm.voicerecorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sm.voicerecorder.ui.VoiceRecorderScreen
import com.sm.voicerecorder.ui.theme.VoiceRecorderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoiceRecorderTheme {
                VoiceRecorderScreen()
            }
        }
    }
}
