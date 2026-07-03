package com.pianoteacher.app.model

import kotlin.math.pow

/**
 * Utility for working with MIDI note numbers.
 * Middle C = C4 = MIDI 60.
 */
object Notes {

    private val NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private val BLACK = setOf(1, 3, 6, 8, 10)

    /** e.g. 60 -> "C4" */
    fun name(midi: Int): String = NAMES[midi % 12] + (midi / 12 - 1)

    /** e.g. 60 -> "C" (no octave) */
    fun shortName(midi: Int): String = NAMES[midi % 12]

    fun isBlack(midi: Int): Boolean = (midi % 12) in BLACK

    fun frequency(midi: Int): Double = 440.0 * 2.0.pow((midi - 69) / 12.0)
}
