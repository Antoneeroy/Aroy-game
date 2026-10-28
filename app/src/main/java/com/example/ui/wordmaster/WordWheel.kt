package com.example.ui.wordmaster

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextPrimary
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun WordWheel(
    letters: List<Char>,
    onWordCompleted: (String) -> Unit,
    onLetterSelected: () -> Unit,
    onShuffle: () -> Unit,
    modifier: Modifier = Modifier,
    wheelSize: Dp = 230.dp
) {
    val selectedIndices = remember { mutableStateListOf<Int>() }
    var currentTouchPoint by remember { mutableStateOf<Offset?>(null) }
    var letterCenters by remember { mutableStateOf<List<Offset>>(emptyList()) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .size(wheelSize)
            .clip(CircleShape)
            .background(GlassSurface)
            .border(1.5.dp, GlassBorder, CircleShape)
            .pointerInput(letters) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentTouchPoint = offset
                        val hitIndex = findLetterAtOffset(offset, letterCenters, 34.dp.toPx())
                        if (hitIndex != -1) {
                            selectedIndices.clear()
                            selectedIndices.add(hitIndex)
                            onLetterSelected()
                        }
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        currentTouchPoint = change.position
                        val hitIndex = findLetterAtOffset(change.position, letterCenters, 34.dp.toPx())
                        if (hitIndex != -1 && !selectedIndices.contains(hitIndex)) {
                            selectedIndices.add(hitIndex)
                            onLetterSelected()
                        }
                    },
                    onDragEnd = {
                        val formed = selectedIndices.map { letters[it] }.joinToString("")
                        if (formed.length >= 2) {
                            onWordCompleted(formed)
                        }
                        selectedIndices.clear()
                        currentTouchPoint = null
                    },
                    onDragCancel = {
                        selectedIndices.clear()
                        currentTouchPoint = null
                    }
                )
            }
            .testTag("word_wheel_canvas"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width * 0.38f
            val count = letters.size

            val centers = (0 until count).map { i ->
                val angle = (i * 2.0 * Math.PI / count) - (Math.PI / 2.0)
                Offset(
                    x = center.x + (radius * cos(angle)).toFloat(),
                    y = center.y + (radius * sin(angle)).toFloat()
                )
            }
            letterCenters = centers

            for (k in 0 until selectedIndices.size - 1) {
                val idx1 = selectedIndices[k]
                val idx2 = selectedIndices[k + 1]
                drawLine(
                    color = CyanAccent,
                    start = centers[idx1],
                    end = centers[idx2],
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
            }

            val touch = currentTouchPoint
            if (selectedIndices.isNotEmpty() && touch != null) {
                val lastCenter = centers[selectedIndices.last()]
                drawLine(
                    color = CyanAccent.copy(alpha = 0.6f),
                    start = lastCenter,
                    end = touch,
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
            }

            for (i in 0 until count) {
                val p = centers[i]
                val isSelected = selectedIndices.contains(i)
                val circleColor = if (isSelected) CyanAccent else GlassSurfaceElevated
                val borderColor = if (isSelected) GoldAccent else Color(0x6638BDF8)

                drawCircle(
                    color = circleColor,
                    radius = 24.dp.toPx(),
                    center = p
                )
                drawCircle(
                    color = borderColor,
                    radius = 24.dp.toPx(),
                    center = p,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                )
            }
        }

        letterCenters.forEachIndexed { i, offset ->
            if (i < letters.size) {
                val isSelected = selectedIndices.contains(i)
                val halfSizePx = with(density) { 24.dp.toPx() }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.TopStart)
                        .offset {
                            IntOffset(
                                (offset.x - halfSizePx).roundToInt(),
                                (offset.y - halfSizePx).roundToInt()
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letters[i].toString(),
                        color = if (isSelected) Color(0xFF0F172A) else TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xD90F172A))
                .border(1.dp, GlassBorder, CircleShape)
                .clickable { onShuffle() }
                .testTag("shuffle_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Shuffle Letters",
                tint = CyanAccent,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

private fun findLetterAtOffset(touch: Offset, centers: List<Offset>, hitRadiusPx: Float): Int {
    for (i in centers.indices) {
        val center = centers[i]
        val dist = hypot((touch.x - center.x).toDouble(), (touch.y - center.y).toDouble())
        if (dist <= hitRadiusPx) {
            return i
        }
    }
    return -1
}
