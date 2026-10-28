package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark Glassmorphism Palette
val DarkBgStart = Color(0xFF090D16)
val DarkBgCenter = Color(0xFF0F172A)
val DarkBgEnd = Color(0xFF131127)

val GlassSurface = Color(0xBF1E293B)
val GlassSurfaceElevated = Color(0xD924334D)
val GlassSurfaceLight = Color(0x33FFFFFF)
val GlassBorder = Color(0x4038BDF8)
val GlassBorderSubtle = Color(0x26FFFFFF)

val CyanAccent = Color(0xFF38BDF8)
val VioletAccent = Color(0xFFA855F7)
val PurpleAccent = Color(0xFF818CF8)
val GoldAccent = Color(0xFFFBBF24)
val EmeraldAccent = Color(0xFF10B981)
val RoseAccent = Color(0xFFF43F5E)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val GlassGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x2E38BDF8),
        Color(0x1EA855F7),
        Color(0x150F172A)
    )
)

val GlowGradient = Brush.horizontalGradient(
    colors = listOf(CyanAccent, VioletAccent)
)

val GoldGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFF59E0B), Color(0xFFFDE047))
)
