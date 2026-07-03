package com.pianoteacher.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PianoColorScheme = darkColorScheme(
    primary = Color(0xFF7AA2F7),
    onPrimary = Color(0xFF15161E),
    secondary = Color(0xFF9ECE6A),
    onSecondary = Color(0xFF15161E),
    tertiary = Color(0xFFE0AF68),
    background = Color(0xFF1A1B26),
    onBackground = Color(0xFFC0CAF5),
    surface = Color(0xFF24283B),
    onSurface = Color(0xFFC0CAF5),
    surfaceVariant = Color(0xFF2E3250),
    onSurfaceVariant = Color(0xFFA9B1D6),
    error = Color(0xFFF7768E)
)

@Composable
fun PianoTeacherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PianoColorScheme,
        content = content
    )
}
