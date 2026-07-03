package com.pianoteacher.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pianoteacher.app.audio.SoundEngine
import com.pianoteacher.app.model.Notes
import com.pianoteacher.app.ui.PianoKeyboard

@Composable
fun FreePlayScreen(
    soundEngine: SoundEngine,
    onBack: () -> Unit
) {
    var showLabels by remember { mutableStateOf(true) }
    val pressed = remember { mutableListOf<Int>().toMutableStateList() }
    var lastNote by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                "Free Play",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.weight(1f))
            lastNote?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(24.dp))
            }
            Text("Labels", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(8.dp))
            Switch(checked = showLabels, onCheckedChange = { showLabels = it })
        }

        PianoKeyboard(
            lowMidi = SoundEngine.LOW_MIDI,
            highMidi = SoundEngine.HIGH_MIDI,
            pressedKeys = pressed.toSet(),
            guideKeys = emptySet(),
            showLabels = showLabels,
            onKeyDown = { midi ->
                pressed.add(midi)
                lastNote = Notes.name(midi)
                soundEngine.play(midi)
            },
            onKeyUp = { midi -> pressed.remove(midi) },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
    }
}
