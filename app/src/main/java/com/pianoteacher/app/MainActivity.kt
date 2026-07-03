package com.pianoteacher.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pianoteacher.app.audio.SoundEngine
import com.pianoteacher.app.model.Song
import com.pianoteacher.app.ui.screens.FreePlayScreen
import com.pianoteacher.app.ui.screens.HomeScreen
import com.pianoteacher.app.ui.screens.LessonScreen
import com.pianoteacher.app.ui.screens.QuizScreen
import com.pianoteacher.app.ui.theme.PianoTeacherTheme

private sealed interface Screen {
    data object Home : Screen
    data object FreePlay : Screen
    data class Lesson(val song: Song) : Screen
    data object Quiz : Screen
}

class MainActivity : ComponentActivity() {

    private lateinit var soundEngine: SoundEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        soundEngine = SoundEngine(applicationContext)

        setContent {
            PianoTeacherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PianoTeacherApp(soundEngine)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundEngine.release()
    }
}

@Composable
private fun PianoTeacherApp(soundEngine: SoundEngine) {
    var soundsReady by remember { mutableStateOf(false) }
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }

    // Kick off tone synthesis once; the engine caches WAVs after first launch.
    LaunchedEffect(Unit) {
        soundEngine.loadAll { soundsReady = true }
    }

    if (!soundsReady) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text("Tuning the piano\u2026", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    BackHandler(enabled = screen != Screen.Home) { screen = Screen.Home }

    when (val current = screen) {
        Screen.Home -> HomeScreen(
            onFreePlay = { screen = Screen.FreePlay },
            onLesson = { song -> screen = Screen.Lesson(song) },
            onQuiz = { screen = Screen.Quiz }
        )
        Screen.FreePlay -> FreePlayScreen(
            soundEngine = soundEngine,
            onBack = { screen = Screen.Home }
        )
        is Screen.Lesson -> LessonScreen(
            song = current.song,
            soundEngine = soundEngine,
            onBack = { screen = Screen.Home }
        )
        Screen.Quiz -> QuizScreen(
            soundEngine = soundEngine,
            onBack = { screen = Screen.Home }
        )
    }
}
