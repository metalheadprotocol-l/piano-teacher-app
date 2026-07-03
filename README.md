# Piano Teacher

An interactive piano teaching app for Android, built with Kotlin and Jetpack Compose.

## Features

- **Interactive piano keyboard** — 3 octaves (C3–C6), full multi-touch with glissando support, optional note-name labels.
- **Free Play** — explore the keyboard freely; the last note you played is shown on screen.
- **Lessons** — guided song learning. The next key lights up in green; play it correctly to advance. Includes Twinkle Twinkle, Mary Had a Little Lamb, Ode to Joy, Happy Birthday, and Jingle Bells. Finish a song to earn up to 3 stars based on how few mistakes you made.
- **Ear Trainer** — the app plays a mystery note and you have to find it on the keyboard. Tracks your score.
- **No audio assets needed** — piano tones are synthesized on-device (additive harmonics with a decay envelope), cached as WAV files on first launch, and played through `SoundPool` for low-latency polyphony.

## Tech stack

- Kotlin 2.1, Jetpack Compose (Material 3)
- Min SDK 26 (Android 8.0), target SDK 35
- Gradle 8.9 / Android Gradle Plugin 8.7

## Building

Open the project in Android Studio (Ladybug or newer) and run the `app` configuration, or from the command line:

```bash
# If the gradle wrapper jar is missing, generate it once with a local Gradle install:
gradle wrapper

# Then build:
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Project structure

```
app/src/main/java/com/pianoteacher/app/
├── MainActivity.kt          # Navigation between screens
├── audio/SoundEngine.kt     # Tone synthesis + SoundPool playback
├── model/
│   ├── Note.kt              # MIDI note names / frequencies
│   └── Song.kt              # Lesson songs with lyrics
└── ui/
    ├── PianoKeyboard.kt     # Multi-touch keyboard composable
    ├── theme/Theme.kt
    └── screens/             # Home, Free Play, Lesson, Quiz
```
