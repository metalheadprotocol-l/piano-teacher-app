package com.pianoteacher.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.pianoteacher.app.model.Notes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.sin

/**
 * Plays piano notes without shipping any audio assets: on first launch it
 * synthesizes a short piano-like tone (additive harmonics + decay envelope)
 * for every key, caches them as WAV files, and plays them via SoundPool
 * for low-latency polyphonic playback.
 */
class SoundEngine(private val context: Context) {

    companion object {
        const val LOW_MIDI = 48   // C3
        const val HIGH_MIDI = 84  // C6
        private const val SAMPLE_RATE = 22050
        private const val DURATION_SEC = 1.6
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(10)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        .build()

    /** midi -> SoundPool sound id */
    private val soundIds = HashMap<Int, Int>()

    fun loadAll(onReady: () -> Unit = {}) {
        scope.launch {
            val dir = File(context.cacheDir, "notes").apply { mkdirs() }
            for (midi in LOW_MIDI..HIGH_MIDI) {
                val file = File(dir, "note_$midi.wav")
                if (!file.exists()) generateWav(file, midi)
                synchronized(soundIds) {
                    soundIds[midi] = soundPool.load(file.absolutePath, 1)
                }
            }
            launch(Dispatchers.Main) { onReady() }
        }
    }

    fun play(midi: Int, volume: Float = 1f) {
        val id = synchronized(soundIds) { soundIds[midi] } ?: return
        soundPool.play(id, volume, volume, 1, 0, 1f)
    }

    fun release() {
        scope.cancel()
        soundPool.release()
    }

    private fun generateWav(file: File, midi: Int) {
        val freq = Notes.frequency(midi)
        val n = (SAMPLE_RATE * DURATION_SEC).toInt()
        // Relative strength of each harmonic; roughly piano-like.
        val harmonics = doubleArrayOf(1.0, 0.5, 0.28, 0.15, 0.07, 0.035)
        val norm = harmonics.sum()
        val pcm = ShortArray(n)

        for (i in 0 until n) {
            val t = i.toDouble() / SAMPLE_RATE
            var v = 0.0
            for (h in harmonics.indices) {
                // Higher harmonics decay faster, which makes the tail sound warmer.
                val hDecay = exp(-t * (2.5 + h * 1.5))
                v += harmonics[h] * hDecay * sin(2.0 * PI * freq * (h + 1) * t)
            }
            val attack = min(1.0, t * 400.0)
            val sample = v / norm * attack * 0.85 * Short.MAX_VALUE
            pcm[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        writeWav(file, pcm)
    }

    private fun writeWav(file: File, pcm: ShortArray) {
        val dataSize = pcm.size * 2
        val buffer = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN)
        buffer.put("RIFF".toByteArray())
        buffer.putInt(36 + dataSize)
        buffer.put("WAVE".toByteArray())
        buffer.put("fmt ".toByteArray())
        buffer.putInt(16)                 // fmt chunk size
        buffer.putShort(1)                // PCM
        buffer.putShort(1)                // mono
        buffer.putInt(SAMPLE_RATE)
        buffer.putInt(SAMPLE_RATE * 2)    // byte rate
        buffer.putShort(2)                // block align
        buffer.putShort(16)               // bits per sample
        buffer.put("data".toByteArray())
        buffer.putInt(dataSize)
        for (s in pcm) buffer.putShort(s)

        RandomAccessFile(file, "rw").use { raf ->
            raf.setLength(0)
            raf.write(buffer.array())
        }
    }
}
