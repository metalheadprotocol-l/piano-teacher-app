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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.pianoteacher.app.ui.PianoKeyboard
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Ear-training quiz: the app plays a note within one octave (C4..C5);
 * the player has to find it on the keyboard.
 */
@Composable
fun QuizScreen(
    soundEngine: SoundEngine,
    onBack: () -> Unit
) {
    val quizLow = 60   // C4
    val quizHigh = 72  // C5

    var targetMidi by remember { mutableIntStateOf(Random.nextInt(quizLow, quizHigh + 1)) }
    var score by remember { mutableIntStateOf(0) }
    var attempts by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("Listen and find the key!") }
    var revealTarget by remember { mutableStateOf(false) }
    var roundKey by remember { mutableIntStateOf(0) }
    val pressed = remember { mutableListOf<Int>().toMutableStateList() }

    // Play the mystery note at the start of every round.
    LaunchedEffect(roundKey) {
        delay(400)
        soundEngine.play(targetMidi)
    }

    fun nextRound() {
        var next: Int
        do {
            next = Random.nextInt(quizLow, quizHigh + 1)
        } while (next == targetMidi)
        targetMidi = next
        revealTarget = false
        feedback = "Listen and find the key!"
        roundKey++
    }

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
                    "Ear Trainer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Score: $score / $attempts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                feedback,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.width(16.dp))
            Button(onClick = { soundEngine.play(targetMidi) }) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Replay")
            }
        }

        PianoKeyboard(
            lowMidi = quizLow,
            highMidi = quizHigh,
            pressedKeys = pressed.toSet(),
            guideKeys = if (revealTarget) setOf(targetMidi) else emptySet(),
            showLabels = true,
            onKeyDown = { midi ->
                pressed.add(midi)
                soundEngine.play(midi)
                if (revealTarget) return@PianoKeyboard
                attempts++
                if (midi == targetMidi) {
                    score++
                    feedback = "Correct! It was ${Notes.name(targetMidi)}"
                    revealTarget = true
                } else {
                    feedback = "Not ${Notes.name(midi)} — try again"
                }
            },
            onKeyUp = { midi -> pressed.remove(midi) },
            modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 8.dp)
        )

        if (revealTarget) {
            Button(
                onClick = { nextRound() },
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
            ) {
                Text("Next note")
            }
        }
    }
}
