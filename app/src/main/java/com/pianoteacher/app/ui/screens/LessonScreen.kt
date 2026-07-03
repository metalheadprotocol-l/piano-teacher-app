package com.pianoteacher.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.pianoteacher.app.model.Song
import com.pianoteacher.app.ui.PianoKeyboard

@Composable
fun LessonScreen(
    song: Song,
    soundEngine: SoundEngine,
    onBack: () -> Unit
) {
    var index by remember(song.id) { mutableIntStateOf(0) }
    var mistakes by remember(song.id) { mutableIntStateOf(0) }
    var wrongFlash by remember { mutableStateOf(false) }
    val pressed = remember { mutableListOf<Int>().toMutableStateList() }

    val finished = index >= song.notes.size
    val target = if (!finished) song.notes[index] else null

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Column(Modifier.weight(1f)) {
                Text(
                    song.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    if (finished) "Complete!" else "Note ${index + 1} of ${song.notes.size} · $mistakes mistakes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!finished && target != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        Notes.name(target.midi),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (wrongFlash) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    if (target.lyric.isNotEmpty()) {
                        Text(
                            "\u201C${target.lyric}\u201D",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        LinearProgressIndicator(
            progress = { index.toFloat() / song.notes.size },
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
        )

        if (finished) {
            LessonComplete(
                song = song,
                mistakes = mistakes,
                onRestart = {
                    index = 0
                    mistakes = 0
                },
                onBack = onBack
            )
        } else {
            PianoKeyboard(
                lowMidi = SoundEngine.LOW_MIDI,
                highMidi = SoundEngine.HIGH_MIDI,
                pressedKeys = pressed.toSet(),
                guideKeys = target?.let { setOf(it.midi) } ?: emptySet(),
                showLabels = false,
                onKeyDown = { midi ->
                    pressed.add(midi)
                    soundEngine.play(midi)
                    if (target != null) {
                        if (midi == target.midi) {
                            wrongFlash = false
                            index++
                        } else {
                            mistakes++
                            wrongFlash = true
                        }
                    }
                },
                onKeyUp = { midi -> pressed.remove(midi) },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
        }
    }
}

@Composable
private fun LessonComplete(
    song: Song,
    mistakes: Int,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    val stars = when {
        mistakes == 0 -> 3
        mistakes <= song.notes.size / 10 + 1 -> 2
        else -> 1
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            repeat(3) { i ->
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (i < stars) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.width(56.dp).height(56.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Well done!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "You played \u201C${song.title}\u201D with $mistakes mistake${if (mistakes == 1) "" else "s"}.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))
        Row {
            OutlinedButton(onClick = onBack) { Text("Back to songs") }
            Spacer(Modifier.width(12.dp))
            Button(onClick = onRestart) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Play again")
            }
        }
    }
}
