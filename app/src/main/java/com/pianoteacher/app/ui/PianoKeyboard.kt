package com.pianoteacher.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import com.pianoteacher.app.model.Notes

private data class KeyRect(val midi: Int, val rect: Rect, val isBlack: Boolean)

private object KeyColors {
    val whiteKey = Color(0xFFF8F8F4)
    val whiteKeyPressed = Color(0xFF7AA2F7)
    val whiteKeyGuide = Color(0xFF9ECE6A)
    val blackKey = Color(0xFF24283B)
    val blackKeyPressed = Color(0xFF7AA2F7)
    val blackKeyGuide = Color(0xFF73A857)
    val outline = Color(0xFF565F89)
    val label = android.graphics.Color.parseColor("#414868")
}

/**
 * Multi-touch piano keyboard. Fingers can slide across keys (glissando);
 * every key change fires [onKeyDown]/[onKeyUp].
 *
 * @param guideKeys keys highlighted in green, used by lesson/quiz modes.
 */
@Composable
fun PianoKeyboard(
    lowMidi: Int,
    highMidi: Int,
    pressedKeys: Set<Int>,
    guideKeys: Set<Int>,
    showLabels: Boolean,
    onKeyDown: (Int) -> Unit,
    onKeyUp: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Recomputed on every draw; stored so the touch handler can hit-test.
    val keyRects = remember { mutableStateOf<List<KeyRect>>(emptyList()) }

    Canvas(
        modifier = modifier.pointerInput(lowMidi, highMidi) {
            awaitPointerEventScope {
                val pointerToKey = HashMap<Long, Int>()
                while (true) {
                    val event = awaitPointerEvent()
                    for (change in event.changes) {
                        val id = change.id.value
                        val previous = pointerToKey[id]
                        if (change.pressed) {
                            val hit = hitTest(keyRects.value, change.position)
                            if (hit != previous) {
                                previous?.let(onKeyUp)
                                if (hit != null) {
                                    pointerToKey[id] = hit
                                    onKeyDown(hit)
                                } else {
                                    pointerToKey.remove(id)
                                }
                            }
                        } else if (previous != null) {
                            pointerToKey.remove(id)
                            onKeyUp(previous)
                        }
                        if (change.positionChanged() || change.pressed != change.previousPressed) {
                            change.consume()
                        }
                    }
                }
            }
        }
    ) {
        val whites = (lowMidi..highMidi).filter { !Notes.isBlack(it) }
        val whiteWidth = size.width / whites.size
        val blackWidth = whiteWidth * 0.62f
        val blackHeight = size.height * 0.6f

        val rects = ArrayList<KeyRect>(highMidi - lowMidi + 1)
        var whiteIndex = 0
        for (midi in lowMidi..highMidi) {
            if (!Notes.isBlack(midi)) {
                rects += KeyRect(
                    midi,
                    Rect(Offset(whiteIndex * whiteWidth, 0f), Size(whiteWidth, size.height)),
                    isBlack = false
                )
                whiteIndex++
            } else {
                // Black key sits on the boundary the previous white key ends at.
                val x = whiteIndex * whiteWidth - blackWidth / 2f
                rects += KeyRect(
                    midi,
                    Rect(Offset(x, 0f), Size(blackWidth, blackHeight)),
                    isBlack = true
                )
            }
        }
        keyRects.value = rects

        // White keys first, black keys on top.
        for (key in rects.filter { !it.isBlack }) {
            val color = when {
                key.midi in pressedKeys -> KeyColors.whiteKeyPressed
                key.midi in guideKeys -> KeyColors.whiteKeyGuide
                else -> KeyColors.whiteKey
            }
            drawRoundRect(
                color = color,
                topLeft = key.rect.topLeft,
                size = key.rect.size,
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(
                color = KeyColors.outline,
                topLeft = key.rect.topLeft,
                size = key.rect.size,
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(width = 2f)
            )
            if (showLabels || key.midi in guideKeys) {
                drawKeyLabel(Notes.name(key.midi), key.rect, whiteWidth)
            }
        }
        for (key in rects.filter { it.isBlack }) {
            val color = when {
                key.midi in pressedKeys -> KeyColors.blackKeyPressed
                key.midi in guideKeys -> KeyColors.blackKeyGuide
                else -> KeyColors.blackKey
            }
            drawRoundRect(
                color = color,
                topLeft = key.rect.topLeft,
                size = key.rect.size,
                cornerRadius = CornerRadius(5f, 5f)
            )
        }
    }
}

private fun hitTest(keys: List<KeyRect>, position: Offset): Int? {
    // Black keys overlap white keys, so test them first.
    keys.firstOrNull { it.isBlack && it.rect.contains(position) }?.let { return it.midi }
    return keys.firstOrNull { !it.isBlack && it.rect.contains(position) }?.midi
}

private fun DrawScope.drawKeyLabel(text: String, rect: Rect, whiteWidth: Float) {
    val paint = android.graphics.Paint().apply {
        color = KeyColors.label
        textSize = (whiteWidth * 0.34f).coerceAtMost(38f)
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
        isFakeBoldText = true
    }
    drawContext.canvas.nativeCanvas.drawText(
        text,
        rect.center.x,
        rect.bottom - rect.height * 0.06f,
        paint
    )
}
