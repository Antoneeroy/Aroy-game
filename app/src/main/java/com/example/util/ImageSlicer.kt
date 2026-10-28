package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import com.example.data.PuzzleDifficulty
import com.example.data.PuzzlePiece
import kotlin.math.hypot
import kotlin.random.Random

object ImageSlicer {

    fun sliceImage(source: Bitmap, difficulty: PuzzleDifficulty, seed: Long = System.currentTimeMillis()): List<PuzzlePiece> {
        val rows = difficulty.rows
        val cols = difficulty.cols
        val total = rows * cols

        val cellW = source.width / cols
        val cellH = source.height / rows
        val tabSize = (maxOf(cellW, cellH) * 0.22f).toInt().coerceAtLeast(12)

        val rng = Random(seed)

        // 1. Generate interlocking edge grid to guarantee perfect mathematical fit
        // Horizontal edges between rows (rows + 1 rows, cols cols)
        // 0 = flat outer border, 1 = points down into lower piece, -1 = points up into upper piece
        val hEdges = Array(rows + 1) { IntArray(cols) { 0 } }
        for (r in 1 until rows) {
            for (c in 0 until cols) {
                hEdges[r][c] = if (rng.nextBoolean()) 1 else -1
            }
        }

        // Vertical edges between columns (rows rows, cols + 1 cols)
        // 0 = flat outer border, 1 = points right into right piece, -1 = points left into left piece
        val vEdges = Array(rows) { IntArray(cols + 1) { 0 } }
        for (r in 0 until rows) {
            for (c in 1 until cols) {
                vEdges[r][c] = if (rng.nextBoolean()) 1 else -1
            }
        }

        val pieces = mutableListOf<PuzzlePiece>()
        var idCounter = 0

        val pieceW = cellW + 2 * tabSize
        val pieceH = cellH + 2 * tabSize

        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)

        val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = Color.argb(160, 255, 255, 255) // 3D highlight rim
        }

        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
            color = Color.argb(140, 10, 15, 30) // Dark interlocking border
        }

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val originX = c * cellW
                val originY = r * cellH

                // Interlocking tabs for this cell
                // Top: opposite of edge between (r-1) and r
                val topTab = -hEdges[r][c]
                // Bottom: edge between r and (r+1)
                val bottomTab = hEdges[r + 1][c]
                // Left: opposite of edge between (c-1) and c
                val leftTab = -vEdges[r][c]
                // Right: edge between c and (c+1)
                val rightTab = vEdges[r][c + 1]

                // Construct authentic Jigsaw Puzzle Piece Path
                val path = createJigsawPath(
                    cellW = cellW.toFloat(),
                    cellH = cellH.toFloat(),
                    pad = tabSize.toFloat(),
                    topTab = topTab,
                    rightTab = rightTab,
                    bottomTab = bottomTab,
                    leftTab = leftTab
                )

                // Create transparent ARGB_8888 bitmap for authentic puzzle cut
                val pieceBitmap = Bitmap.createBitmap(pieceW, pieceH, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(pieceBitmap)

                // Clip to the puzzle shape so it cuts into a genuine jigsaw piece
                canvas.clipPath(path)

                // Draw source sub-region with clean alignment
                canvas.drawBitmap(
                    source,
                    (tabSize - originX).toFloat(),
                    (tabSize - originY).toFloat(),
                    paint
                )

                // Draw 3D tactile bevel along the jigsaw outline
                canvas.drawPath(path, shadowPaint)
                canvas.drawPath(path, rimPaint)

                val isEdge = (r == 0 || r == rows - 1 || c == 0 || c == cols - 1)
                val initialRotation = if (difficulty.hasRotation) {
                    rng.nextInt(4) * 90
                } else {
                    0
                }

                val finalBitmap = if (initialRotation != 0) {
                    rotateBitmap(pieceBitmap, initialRotation.toFloat())
                } else {
                    pieceBitmap
                }

                val piece = PuzzlePiece(
                    id = idCounter,
                    correctIndex = idCounter,
                    targetRow = r,
                    targetCol = c,
                    isEdge = isEdge,
                    bitmap = finalBitmap,
                    rotation = initialRotation,
                    isPlaced = false,
                    placedSlotIndex = null,
                    topTab = topTab,
                    rightTab = rightTab,
                    bottomTab = bottomTab,
                    leftTab = leftTab
                )
                pieces.add(piece)
                idCounter++
            }
        }

        return pieces.shuffled(rng)
    }

    /**
     * Creates a closed Path representing a classic interlocking Jigsaw Puzzle piece.
     * Corners are at:
     * Top-Left: (pad, pad)
     * Top-Right: (pad + cellW, pad)
     * Bottom-Right: (pad + cellW, pad + cellH)
     * Bottom-Left: (pad, pad + cellH)
     */
    fun createJigsawPath(
        cellW: Float,
        cellH: Float,
        pad: Float,
        topTab: Int,
        rightTab: Int,
        bottomTab: Int,
        leftTab: Int
    ): Path {
        val path = Path()

        val tlX = pad
        val tlY = pad
        val trX = pad + cellW
        val trY = pad
        val brX = pad + cellW
        val brY = pad + cellH
        val blX = pad
        val blY = pad + cellH

        path.moveTo(tlX, tlY)

        // Top edge: Top-Left to Top-Right
        addJigsawEdge(path, tlX, tlY, trX, trY, topTab)

        // Right edge: Top-Right to Bottom-Right
        addJigsawEdge(path, trX, trY, brX, brY, rightTab)

        // Bottom edge: Bottom-Right to Bottom-Left
        addJigsawEdge(path, brX, brY, blX, blY, bottomTab)

        // Left edge: Bottom-Left to Top-Left
        addJigsawEdge(path, blX, blY, tlX, tlY, leftTab)

        path.close()
        return path
    }

    /**
     * Adds an authentic jigsaw puzzle edge with neck and knob to the path.
     * tabDir: 0 = flat line, 1 = outward knob, -1 = inward hole
     */
    private fun addJigsawEdge(
        path: Path,
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        tabDir: Int
    ) {
        if (tabDir == 0) {
            path.lineTo(x1, y1)
            return
        }

        val dx = x1 - x0
        val dy = y1 - y0
        val length = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        if (length == 0f) return

        val ux = dx / length
        val uy = dy / length

        // Normal pointing to the left of the forward travel direction
        val nx = -uy * tabDir
        val ny = ux * tabDir
        val tabH = length * 0.22f

        // Helper to compute coordinate for forward ratio s and normal ratio t
        fun ptX(s: Float, t: Float): Float = x0 + s * length * ux + t * tabH * nx
        fun ptY(s: Float, t: Float): Float = y0 + s * length * uy + t * tabH * ny

        // 1. Straight base to start of tab neck
        path.lineTo(ptX(0.35f, 0f), ptY(0.35f, 0f))

        // 2. Neck curve indentation
        path.cubicTo(
            ptX(0.36f, -0.06f), ptY(0.36f, -0.06f),
            ptX(0.32f, 0.35f), ptY(0.32f, 0.35f),
            ptX(0.38f, 0.72f), ptY(0.38f, 0.72f)
        )

        // 3. Rounded circular knob head
        path.cubicTo(
            ptX(0.44f, 1.08f), ptY(0.44f, 1.08f),
            ptX(0.56f, 1.08f), ptY(0.56f, 1.08f),
            ptX(0.62f, 0.72f), ptY(0.62f, 0.72f)
        )

        // 4. Neck curve return to base line
        path.cubicTo(
            ptX(0.68f, 0.35f), ptY(0.68f, 0.35f),
            ptX(0.64f, -0.06f), ptY(0.64f, -0.06f),
            ptX(0.65f, 0f), ptY(0.65f, 0f)
        )

        // 5. Straight base to destination corner
        path.lineTo(x1, y1)
    }

    /**
     * Rotates a bitmap by 90 degrees clockwise
     */
    fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
        if (degrees % 360 == 0f) return source
        val matrix = Matrix().apply {
            postRotate(degrees)
        }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}
