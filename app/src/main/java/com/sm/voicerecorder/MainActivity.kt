package com.sm.voicerecorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sm.voicerecorder.ui.VoiceRecorderScreen
import com.sm.voicerecorder.ui.theme.VoiceRecorderTheme
import com.sm.voicerecorder.viewmodel.RecorderViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: RecorderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val profile by viewModel.profile.collectAsState()
            VoiceRecorderTheme(profile = profile) {
                VoiceRecorderScreen(viewModel = viewModel)
            }
        }
    }
}
