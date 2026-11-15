package com.sm.voicerecorder.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sm.voicerecorder.model.AppProfile
import com.sm.voicerecorder.ui.components.RecordingItemCard
import com.sm.voicerecorder.util.Formatters
import com.sm.voicerecorder.viewmodel.RecorderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceRecorderScreen(
    viewModel: RecorderViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val profile by viewModel.profile.collectAsState()
    val isKids = profile == AppProfile.KIDS

    val recordings by viewModel.recordings.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingDuration by viewModel.recordingDurationSeconds.collectAsState()
    val childName by viewModel.childName.collectAsState()

    val playingFile by viewModel.playingFile.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackProgressMs by viewModel.playbackProgressMs.collectAsState()
    val playbackDurationMs by viewModel.playbackDurationMs.collectAsState()

    var showNameDialog by remember { mutableStateOf(false) }

    var hasRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasRecordPermission = isGranted
        if (isGranted) {
            viewModel.startRecording()
        }
    }

    fun handleRecordButtonClick() {
        if (isRecording) {
            viewModel.stopRecording()
        } else {
            if (hasRecordPermission) {
                viewModel.startRecording()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    // Botão para alternar entre Perfis (Adulto vs Infantil)
                    IconButton(
                        onClick = {
                            viewModel.setProfile(if (isKids) AppProfile.DEFAULT else AppProfile.KIDS)
                        }
                    ) {
                        if (isKids) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Mudar para Modo Padrão",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "Mudar para Modo Mágico",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isKids) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Text(
                                text = if (isKids) {
                                    if (childName.isNotBlank()) "Gravador da $childName" else "Gravador Mágico"
                                } else {
                                    "Gravador de Voz"
                                },
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )

                            if (isKids) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Filled.Palette,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isKids) {
                                if (recordings.isEmpty()) "Pronto para a mágica!" else "${recordings.size} áudios encantados ✨"
                            } else {
                                if (recordings.isEmpty()) "Nenhuma gravação" else "${recordings.size} gravações salvas"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (isKids) {
                        IconButton(onClick = { showNameDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Editar nome",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            RecordingBottomBar(
                isKids = isKids,
                isRecording = isRecording,
                recordingDurationSeconds = recordingDuration,
                onRecordClick = { handleRecordButtonClick() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (recordings.isEmpty()) {
                // Estado Vazio (Customizado por Perfil)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isKids) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(110.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(54.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = if (childName.isNotBlank()) "Oi, $childName! Pronta para a mágica? 🪄🎨" else "Pronta para criar mágica? 🪄🎨",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Toque no botão mágico abaixo para gravar historinhas, canções e segredos encantados!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.MicNone,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Nenhuma gravação ainda",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Toque no botão de microfone abaixo para iniciar uma nova gravação de áudio.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Lista de Gravações
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recordings, key = { it.file.absolutePath }) { recording ->
                        val isCurrent = playingFile?.absolutePath == recording.file.absolutePath
                        RecordingItemCard(
                            recording = recording,
                            isCurrent = isCurrent,
                            isPlaying = isCurrent && isPlaying,
                            currentProgressMs = if (isCurrent) playbackProgressMs else 0,
                            playbackDurationMs = if (isCurrent) playbackDurationMs else 0,
                            onPlayPauseClick = { viewModel.togglePlayPause(recording) },
                            onSeek = { viewModel.seekTo(it) },
                            onShareClick = { viewModel.shareRecording(context, recording) },
                            onDeleteClick = { viewModel.deleteRecording(recording) },
                            profile = profile
                        )
                    }
                }
            }
        }
    }

    if (showNameDialog) {
        var tempName by remember { mutableStateOf(childName) }

        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Nome da Artista 🪄")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Digite o nome para personalizar o gravador mágico:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        placeholder = { Text("Ex: Clara, Alice, Sofia...") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateChildName(tempName)
                        showNameDialog = false
                    }
                ) {
                    Text(
                        text = "Salvar ✨",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
}

@Composable
fun RecordingBottomBar(
    isKids: Boolean,
    isRecording: Boolean,
    recordingDurationSeconds: Long,
    onRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        tonalElevation = if (isKids) 10.dp else 6.dp,
        shadowElevation = if (isKids) 12.dp else 6.dp,
        shape = if (isKids) RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp) else RoundedCornerShape(0.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = if (isKids) 18.dp else 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Efeito visual enquanto grava
            AnimatedVisibility(visible = isRecording) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 0.85f,
                    targetValue = 1.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseScale"
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .then(
                                if (isKids) {
                                    Modifier.background(
                                        Brush.radialGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.secondary,
                                                MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    )
                                } else {
                                    Modifier.background(Color(0xFFE53935))
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isKids) {
                            "Gravando mágica... ✨ ${Formatters.formatDurationSeconds(recordingDurationSeconds)}"
                        } else {
                            "Gravando: ${Formatters.formatDurationSeconds(recordingDurationSeconds)}"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isKids) MaterialTheme.colorScheme.primary else Color(0xFFE53935)
                        )
                    )
                }
            }

            // Botão Principal (Gravar / Parar)
            FilledIconButton(
                onClick = onRecordClick,
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = when {
                        isRecording -> if (isKids) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                        isKids -> MaterialTheme.colorScheme.primary
                        else -> Color(0xFFE53935)
                    }
                ),
                modifier = Modifier.size(if (isKids) 72.dp else 64.dp)
            ) {
                Icon(
                    imageVector = when {
                        isRecording -> Icons.Filled.Stop
                        isKids -> Icons.Filled.AutoAwesome
                        else -> Icons.Filled.Mic
                    },
                    contentDescription = if (isRecording) "Parar" else "Gravar",
                    tint = Color.White,
                    modifier = Modifier.size(if (isKids) 36.dp else 32.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when {
                    isRecording && isKids -> "Toque para guardar o áudio ✨"
                    isRecording -> "Toque para parar e salvar"
                    isKids -> "Toque na varinha para gravar 🪄"
                    else -> "Toque para gravar"
                },
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
