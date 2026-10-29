package com.example.data

import android.graphics.Bitmap

enum class PuzzleDifficulty(
    val title: String,
    val rows: Int,
    val cols: Int,
    val totalPieces: Int,
    val defaultTimerSec: Int,
    val hasRotation: Boolean,
    val ghostOpacityDefault: Float
) {
    EASY("Easy (16 Pieces)", 4, 4, 16, 0, false, 0.35f),
    MEDIUM("Medium (36 Pieces)", 6, 6, 36, 180, false, 0.20f),
    HARD("Hard (48 Pieces)", 8, 6, 48, 240, true, 0.0f),
    EXPERT("Custom (64 Pieces)", 8, 8, 64, 300, false, 0.25f);
}

enum class PuzzleCategory(
    val displayName: String,
    val iconEmoji: String,
    val subtitle: String,
    val seedPrefix: String
) {
    ANIMALS("Animals", "🦁", "Wild & majestic animals", "aroy_animal"),
    FLOWERS("Flowers", "🌸", "Roses, lotus, orchids & lilies", "aroy_flower"),
    BIRDS("Birds", "🦚", "Peacocks, parrots & eagles", "aroy_bird"),
    NATURE("Nature", "🏔️", "Mountains, waterfalls & beaches", "aroy_nature"),
    SPACE("Space", "🌌", "Galaxies, nebulae & planets", "aroy_space"),
    MARINE("Marine Life", "🐬", "Dolphins, turtles & coral reefs", "aroy_marine"),
    FOREST("Forests", "🌲", "Misty woods, redwoods & trees", "aroy_forest"),
    CASTLES("Castles", "🏰", "Fairytale castles & landmarks", "aroy_castle"),
    CARS("Supercars", "🏎️", "High-speed hypercars & racers", "aroy_car");

    fun getImageUrl(level: Int): String {
        // High quality crystal HD seed images from Picsum and curated source
        return "https://picsum.photos/seed/${seedPrefix}_lvl_${level}/1024/1024"
    }

    companion object {
        fun forLevel(level: Int): PuzzleCategory {
            val list = entries
            return list[(level - 1) % list.size]
        }
    }
}

data class PuzzlePiece(
    val id: Int,
    val correctIndex: Int,
    val targetRow: Int,
    val targetCol: Int,
    val isEdge: Boolean,
    val bitmap: Bitmap,
    val rotation: Int = 0, // 0, 90, 180, 270
    val isPlaced: Boolean = false,
    val placedSlotIndex: Int? = null,
    val topTab: Int = 0,
    val rightTab: Int = 0,
    val bottomTab: Int = 0,
    val leftTab: Int = 0
)

data class PhotoPuzzleLevelConfig(
    val levelNumber: Int,
    val difficulty: PuzzleDifficulty,
    val category: PuzzleCategory,
    val title: String,
    val imageUrl: String,
    val customImageUri: String? = null
)

enum class LevelSelectTarget {
    PHOTO_EASY,
    PHOTO_MEDIUM,
    PHOTO_HARD,
    PHOTO_EXPERT
}

