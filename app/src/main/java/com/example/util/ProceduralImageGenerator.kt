package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import com.example.data.PuzzleCategory
import kotlin.random.Random

/**
 * Generates beautiful, rich vector-like HD bitmaps locally for instant offline play
 * and seamless fallback before network images load.
 */
object ProceduralImageGenerator {

    fun generateCategoryBitmap(category: PuzzleCategory, seed: Int, width: Int = 800, height: Int = 800): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rng = Random(seed * 7919)

        when (category) {
            PuzzleCategory.SPACE -> drawSpaceScene(canvas, paint, width, height, rng)
            PuzzleCategory.NATURE -> drawNatureScene(canvas, paint, width, height, rng)
            PuzzleCategory.MARINE -> drawMarineScene(canvas, paint, width, height, rng)
            PuzzleCategory.FOREST -> drawForestScene(canvas, paint, width, height, rng)
            PuzzleCategory.FLOWERS -> drawFlowerScene(canvas, paint, width, height, rng)
            PuzzleCategory.BIRDS -> drawBirdScene(canvas, paint, width, height, rng)
            PuzzleCategory.ANIMALS -> drawAnimalScene(canvas, paint, width, height, rng)
        }

        return bitmap
    }

    private fun drawSpaceScene(canvas: Canvas, paint: Paint, w: Int, h: Int, rng: Random) {
        // Deep space nebula gradient
        paint.shader = RadialGradient(
            w * 0.5f, h * 0.5f, w * 0.7f,
            intArrayOf(Color.rgb(120, 20, 160), Color.rgb(20, 15, 60), Color.rgb(5, 5, 20)),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        // Glowing nebula clouds
        paint.shader = null
        paint.color = Color.argb(40, 56, 189, 248)
        canvas.drawCircle(w * 0.3f, h * 0.4f, w * 0.28f, paint)
        paint.color = Color.argb(45, 236, 72, 153)
        canvas.drawCircle(w * 0.7f, h * 0.6f, w * 0.32f, paint)

        // Distant stars
        paint.color = Color.WHITE
        for (i in 0..120) {
            val sx = rng.nextFloat() * w
            val sy = rng.nextFloat() * h
            val sr = rng.nextFloat() * 3.5f + 1f
            paint.alpha = rng.nextInt(120, 255)
            canvas.drawCircle(sx, sy, sr, paint)
        }

        // Glowing planet with ring
        val px = w * 0.65f
        val py = h * 0.35f
        val pr = w * 0.16f
        paint.shader = LinearGradient(
            px - pr, py - pr, px + pr, py + pr,
            Color.rgb(245, 158, 11), Color.rgb(180, 83, 9), Shader.TileMode.CLAMP
        )
        canvas.drawCircle(px, py, pr, paint)

        // Planet ring
        paint.shader = null
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 14f
        paint.color = Color.argb(180, 253, 224, 71)
        canvas.drawOval(px - pr * 1.7f, py - pr * 0.4f, px + pr * 1.7f, py + pr * 0.4f, paint)
        paint.style = Paint.Style.FILL
    }

    private fun drawNatureScene(canvas: Canvas, paint: Paint, w: Int, h: Int, rng: Random) {
        // Sunset sky gradient
        paint.shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            intArrayOf(Color.rgb(249, 115, 22), Color.rgb(234, 179, 8), Color.rgb(14, 165, 233)),
            floatArrayOf(0f, 0.45f, 1f), Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        // Golden Sun
        paint.shader = null
        paint.color = Color.argb(220, 254, 240, 138)
        canvas.drawCircle(w * 0.5f, h * 0.42f, w * 0.14f, paint)

        // Mountain Layers
        drawMountainLayer(canvas, paint, w, h, h * 0.45f, Color.rgb(67, 56, 202), 4, rng)
        drawMountainLayer(canvas, paint, w, h, h * 0.58f, Color.rgb(30, 27, 75), 5, rng)

        // Water reflection
        paint.color = Color.argb(200, 15, 23, 42)
        canvas.drawRect(0f, h * 0.72f, w.toFloat(), h.toFloat(), paint)
        paint.color = Color.argb(60, 253, 224, 71)
        canvas.drawRect(w * 0.35f, h * 0.72f, w * 0.65f, h.toFloat(), paint)
    }

    private fun drawMountainLayer(canvas: Canvas, paint: Paint, w: Int, h: Int, baseY: Float, color: Int, peaks: Int, rng: Random) {
        paint.shader = null
        paint.color = color
        val path = Path()
        path.moveTo(0f, h.toFloat())
        path.lineTo(0f, baseY)
        val step = w.toFloat() / peaks
        for (i in 1..peaks) {
            val px = (i - 0.5f) * step
            val py = baseY - (rng.nextFloat() * 140f + 60f)
            path.lineTo(px, py)
            path.lineTo(i * step, baseY + rng.nextFloat() * 30f)
        }
        path.lineTo(w.toFloat(), h.toFloat())
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawMarineScene(canvas: Canvas, paint: Paint, w: Int, h: Int, rng: Random) {
        // Deep turquoise ocean gradient
        paint.shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            intArrayOf(Color.rgb(6, 182, 212), Color.rgb(2, 132, 199), Color.rgb(15, 23, 42)),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        // Coral silhouettes at bottom
        paint.shader = null
        paint.color = Color.rgb(236, 72, 153)
        canvas.drawCircle(w * 0.2f, h * 0.9f, w * 0.18f, paint)
        paint.color = Color.rgb(168, 85, 247)
        canvas.drawCircle(w * 0.75f, h * 0.95f, w * 0.22f, paint)

        // Marine bubbles
        paint.color = Color.argb(120, 255, 255, 255)
        for (i in 0..40) {
            val bx = rng.nextFloat() * w
            val by = rng.nextFloat() * h * 0.8f
            val br = rng.nextFloat() * 12f + 4f
            canvas.drawCircle(bx, by, br, paint)
        }

        // Tropical clownfish / ray silhouette
        paint.color = Color.rgb(249, 115, 22)
        canvas.drawOval(w * 0.45f, h * 0.45f, w * 0.62f, h * 0.55f, paint)
        val tail = Path().apply {
            moveTo(w * 0.45f, h * 0.5f)
            lineTo(w * 0.38f, h * 0.43f)
            lineTo(w * 0.38f, h * 0.57f)
            close()
        }
        canvas.drawPath(tail, paint)
    }

    private fun drawForestScene(canvas: Canvas, paint: Paint, w: Int, h: Int, rng: Random) {
        // Emerald twilight
        paint.shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            intArrayOf(Color.rgb(16, 185, 129), Color.rgb(5, 150, 105), Color.rgb(6, 78, 59)),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        // Forest tree trunks and canopy
        paint.shader = null
        paint.color = Color.argb(180, 2, 44, 34)
        for (i in 0..8) {
            val tx = (i + 0.5f) * (w / 8f) + rng.nextFloat() * 20f - 10f
            canvas.drawRect(tx - 18f, h * 0.3f, tx + 18f, h.toFloat(), paint)
            canvas.drawCircle(tx, h * 0.28f, w * 0.12f, paint)
        }
        // Sunbeams
        paint.color = Color.argb(45, 253, 224, 71)
        val beam = Path().apply {
            moveTo(w * 0.5f, 0f)
            lineTo(w * 0.1f, h.toFloat())
            lineTo(w * 0.35f, h.toFloat())
            close()
        }
        canvas.drawPath(beam, paint)
    }

    private fun drawFlowerScene(canvas: Canvas, paint: Paint, w: Int, h: Int, rng: Random) {
        // Garden soft purple-pink background
        paint.shader = LinearGradient(
            0f, 0f, w.toFloat(), h.toFloat(),
            intArrayOf(Color.rgb(244, 114, 182), Color.rgb(168, 85, 247), Color.rgb(99, 102, 241)),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        // Glowing central flower mandala
        val cx = w * 0.5f
        val cy = h * 0.5f
        val petalCount = 8
        val petalRadius = w * 0.28f
        paint.shader = null

        for (i in 0 until petalCount) {
            val angle = (i * 2 * Math.PI / petalCount).toFloat()
            val px = cx + (Math.cos(angle.toDouble()) * petalRadius * 0.5).toFloat()
            val py = cy + (Math.sin(angle.toDouble()) * petalRadius * 0.5).toFloat()
            paint.color = Color.argb(190, 253, 242, 248)
            canvas.drawCircle(px, py, w * 0.14f, paint)
        }

        // Flower golden core
        paint.color = Color.rgb(245, 158, 11)
        canvas.drawCircle(cx, cy, w * 0.11f, paint)
        paint.color = Color.rgb(254, 240, 138)
        canvas.drawCircle(cx, cy, w * 0.06f, paint)
    }

    private fun drawBirdScene(canvas: Canvas, paint: Paint, w: Int, h: Int, rng: Random) {
        // Dawn Sky
        paint.shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            intArrayOf(Color.rgb(14, 165, 233), Color.rgb(168, 85, 247), Color.rgb(251, 191, 36)),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        // Majestic bird in flight silhouette
        paint.shader = null
        paint.color = Color.rgb(15, 23, 42)
        val cx = w * 0.5f
        val cy = h * 0.42f
        val bird = Path().apply {
            moveTo(cx, cy)
            // Left wing
            quadTo(cx - w * 0.22f, cy - h * 0.18f, cx - w * 0.38f, cy - h * 0.1f)
            quadTo(cx - w * 0.2f, cy - h * 0.05f, cx, cy + 20f)
            // Right wing
            quadTo(cx + w * 0.22f, cy - h * 0.18f, cx + w * 0.38f, cy - h * 0.1f)
            quadTo(cx + w * 0.2f, cy - h * 0.05f, cx, cy)
            close()
        }
        canvas.drawPath(bird, paint)
    }

    private fun drawAnimalScene(canvas: Canvas, paint: Paint, w: Int, h: Int, rng: Random) {
        // Savanna sunset
        paint.shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            intArrayOf(Color.rgb(220, 38, 38), Color.rgb(245, 158, 11), Color.rgb(254, 240, 138)),
            null, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        // Acacia tree silhouette
        paint.shader = null
        paint.color = Color.rgb(15, 23, 42)
        canvas.drawRect(w * 0.65f, h * 0.5f, w * 0.72f, h * 0.85f, paint)
        canvas.drawOval(w * 0.5f, h * 0.4f, w * 0.88f, h * 0.54f, paint)

        // Ground
        canvas.drawRect(0f, h * 0.82f, w.toFloat(), h.toFloat(), paint)
    }
}
