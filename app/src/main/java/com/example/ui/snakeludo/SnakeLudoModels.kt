package com.example.ui.snakeludo

import androidx.compose.ui.graphics.Color

enum class SnakeDifficulty(
    val displayName: String,
    val snakeCount: Int,
    val ladderCount: Int,
    val coinReward: Int,
    val description: String
) {
    EASY("Easy", 4, 8, 30, "More ladders, fewer snakes • Great for beginners"),
    MIDDLE("Middle", 6, 6, 45, "Balanced snakes and ladders • Classic rules"),
    HARD("Hard", 8, 5, 65, "More snakes, steep ladders • Challenging climb"),
    VERY_HARD("Very Hard", 11, 4, 100, "Venomous board, aggressive AI • Grandmaster difficulty")
}

enum class PlayerMode(
    val displayName: String,
    val playerCount: Int,
    val description: String,
    val icon: String
) {
    SOLO_VS_AI("Solo vs AI", 2, "You vs Smart Robot AI", "🤖"),
    TWO_PLAYERS("2 Players", 2, "2 Office Colleagues (Pass & Play)", "👥"),
    THREE_PLAYERS("3 Players", 3, "3 Office Colleagues (Pass & Play)", "👨‍👧‍👦"),
    FOUR_PLAYERS("4 Players", 4, "4 Office Colleagues (Pass & Play)", "🏆")
}

data class LudoPlayer(
    val id: Int,
    val name: String,
    val color: Color,
    val avatarIcon: String,
    val pos: Int = 1,
    val isAi: Boolean = false,
    val rank: Int? = null
)

data class Snake(val head: Int, val tail: Int)
data class Ladder(val foot: Int, val top: Int)

enum class SnakeTheme(
    val title: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val boardBg: Color,
    val cellEven: Color,
    val cellOdd: Color,
    val snakeColor: Color,
    val ladderColor: Color,
    val accentIcon: String,
    val boardDrawableRes: Int? = null,
    val isClassicPalette: Boolean = false,
    val tilePalette: List<Color> = listOf(
        Color(0xFFE53935), Color(0xFF0284C7), Color(0xFFF97316), Color(0xFF16A34A), Color(0xFFFACC15)
    )
) {
    CLASSIC_RETRO(
        "Classic Board",
        Color(0xFFE53935),
        Color(0xFFFACC15),
        Color(0xFF0F172A),
        Color(0xFFE53935),
        Color(0xFF1E88E5),
        Color(0xFF16A34A),
        Color(0xFF854D0E),
        "🎲",
        boardDrawableRes = com.example.R.drawable.classic_board_grid,
        isClassicPalette = true,
        tilePalette = listOf(
            Color(0xFFDC2626), // Rich Red
            Color(0xFF0284C7), // Royal Blue
            Color(0xFFF97316), // Vivid Orange
            Color(0xFF16A34A), // Forest Green
            Color(0xFFFACC15)  // Golden Yellow
        )
    ),
    NEON_LUDO(
        "Neon Ludo",
        Color(0xFF38BDF8),
        Color(0xFFA855F7),
        Color(0xFF0B1120),
        Color(0xFF1E293B),
        Color(0xFF131D33),
        Color(0xFFEF4444),
        Color(0xFF10B981),
        "⚡",
        tilePalette = listOf(
            Color(0xFF6366F1), // Electric Indigo
            Color(0xFF06B6D4), // Cyan Glow
            Color(0xFFEC4899), // Hot Magenta
            Color(0xFF10B981), // Emerald Neon
            Color(0xFFF59E0B)  // Amber Flare
        )
    ),
    JUNGLE_SAFARI(
        "Jungle Safari",
        Color(0xFF10B981),
        Color(0xFF84CC16),
        Color(0xFF062316),
        Color(0xFF0D3B26),
        Color(0xFF092E1E),
        Color(0xFFDC2626),
        Color(0xFFF59E0B),
        "🌿",
        tilePalette = listOf(
            Color(0xFF15803D), // Lush Forest
            Color(0xFF0284C7), // River Stream
            Color(0xFFD97706), // Sunlit Bark
            Color(0xFF16A34A), // Jungle Canopy
            Color(0xFFCA8A04)  // Golden Orchid
        )
    ),
    SPACE_GALAXY(
        "Space Galaxy",
        Color(0xFF818CF8),
        Color(0xFFC084FC),
        Color(0xFF080B1A),
        Color(0xFF1A1B3B),
        Color(0xFF11122B),
        Color(0xFFF43F5E),
        Color(0xFF38BDF8),
        "🪐",
        tilePalette = listOf(
            Color(0xFF4F46E5), // Deep Space Blue
            Color(0xFF9333EA), // Nebula Violet
            Color(0xFFE11D48), // Solar Flare Red
            Color(0xFF0EA5E9), // Cosmic Azure
            Color(0xFFF59E0B)  // Stellar Gold
        )
    ),
    DESERT_KINGDOM(
        "Desert Kingdom",
        Color(0xFFF59E0B),
        Color(0xFFD97706),
        Color(0xFF1E1408),
        Color(0xFF3D2A10),
        Color(0xFF2A1C0A),
        Color(0xFFEF4444),
        Color(0xFFFDE047),
        "🏜️",
        tilePalette = listOf(
            Color(0xFFB45309), // Terracotta
            Color(0xFF0284C7), // Oasis Blue
            Color(0xFFEA580C), // Desert Sunset
            Color(0xFF65A30D), // Palm Green
            Color(0xFFFBBF24)  // Golden Sand
        )
    ),
    OCEAN_CORAL(
        "Ocean Coral",
        Color(0xFF06B6D4),
        Color(0xFF0284C7),
        Color(0xFF051B2C),
        Color(0xFF0C3854),
        Color(0xFF08283D),
        Color(0xFFFB7185),
        Color(0xFF34D399),
        "🌊",
        tilePalette = listOf(
            Color(0xFF0369A1), // Deep Ocean Blue
            Color(0xFF0D9488), // Seafoam Teal
            Color(0xFFF43F5E), // Coral Reef Pink
            Color(0xFF0284C7), // Azure Tide
            Color(0xFFFBBF24)  // Sunlit Shell
        )
    ),
    ENCHANTED_FOREST(
        "Enchanted Forest",
        Color(0xFFA855F7),
        Color(0xFFEC4899),
        Color(0xFF1A0A26),
        Color(0xFF33164A),
        Color(0xFF240E36),
        Color(0xFFF43F5E),
        Color(0xFF4ADE80),
        "✨",
        tilePalette = listOf(
            Color(0xFF7E22CE), // Mystical Violet
            Color(0xFF059669), // Enchanted Emerald
            Color(0xFFDB2777), // Pixie Magenta
            Color(0xFF2563EB), // Starlight Sapphire
            Color(0xFFEAB308)  // Fairy Dust Gold
        )
    ),
    CYBER_MATRIX(
        "Cyber Matrix",
        Color(0xFF22C55E),
        Color(0xFF10B981),
        Color(0xFF05170B),
        Color(0xFF0E381C),
        Color(0xFF092412),
        Color(0xFFEF4444),
        Color(0xFF22D3EE),
        "💻",
        tilePalette = listOf(
            Color(0xFF047857), // Terminal Emerald
            Color(0xFF0284C7), // Circuit Cyan
            Color(0xFFE11D48), // Core Alarm Red
            Color(0xFF16A34A), // Matrix Green
            Color(0xFFEAB308)  // Data Gold
        )
    ),
    VOLCANO_MAGMA(
        "Volcano Magma",
        Color(0xFFF97316),
        Color(0xFFEA580C),
        Color(0xFF260D05),
        Color(0xFF471A0B),
        Color(0xFF331207),
        Color(0xFFE11D48),
        Color(0xFFFACC15),
        "🌋",
        tilePalette = listOf(
            Color(0xFFB91C1C), // Crimson Magma
            Color(0xFF0284C7), // Geothermal Stream
            Color(0xFFC2410C), // Molten Lava
            Color(0xFF15803D), // Volcanic Fern
            Color(0xFFF59E0B)  // Blazing Amber
        )
    ),
    ARCTIC_ICE(
        "Arctic Ice",
        Color(0xFF38BDF8),
        Color(0xFFE0F2FE),
        Color(0xFF081829),
        Color(0xFF123454),
        Color(0xFF0D253D),
        Color(0xFFF43F5E),
        Color(0xFF67E8F9),
        "❄️",
        tilePalette = listOf(
            Color(0xFF0284C7), // Glacial Azure
            Color(0xFF0E7490), // Polar Deep Blue
            Color(0xFFE11D48), // Beacon Crimson
            Color(0xFF0D9488), // Aurora Borealis Green
            Color(0xFFFBBF24)  // Winter Sun Gold
        )
    ),
    GOLDEN_TEMPLE(
        "Golden Temple",
        Color(0xFFFBBF24),
        Color(0xFFF59E0B),
        Color(0xFF1F1803),
        Color(0xFF3D3008),
        Color(0xFF2B2205),
        Color(0xFFDC2626),
        Color(0xFFFDE047),
        "👑",
        tilePalette = listOf(
            Color(0xFFB45309), // Royal Bronze
            Color(0xFF0284C7), // Temple Pool Blue
            Color(0xFFDC2626), // Sacred Scarlet
            Color(0xFF15803D), // Garland Green
            Color(0xFFEAB308)  // Pure Temple Gold
        )
    )
}

object BoardConfigGenerator {
    fun getSnakesAndLadders(difficulty: SnakeDifficulty): Pair<List<Snake>, List<Ladder>> {
        return when (difficulty) {
            SnakeDifficulty.EASY -> Pair(
                listOf(
                    Snake(head = 95, tail = 75),
                    Snake(head = 78, tail = 58),
                    Snake(head = 54, tail = 34),
                    Snake(head = 38, tail = 18)
                ),
                listOf(
                    Ladder(foot = 4, top = 25),
                    Ladder(foot = 9, top = 31),
                    Ladder(foot = 21, top = 42),
                    Ladder(foot = 28, top = 50),
                    Ladder(foot = 35, top = 57),
                    Ladder(foot = 51, top = 72),
                    Ladder(foot = 71, top = 91),
                    Ladder(foot = 80, top = 99)
                )
            )
            SnakeDifficulty.MIDDLE -> Pair(
                listOf(
                    Snake(head = 98, tail = 79),
                    Snake(head = 87, tail = 68),
                    Snake(head = 65, tail = 45),
                    Snake(head = 54, tail = 34),
                    Snake(head = 43, tail = 22),
                    Snake(head = 26, tail = 8)
                ),
                listOf(
                    Ladder(foot = 5, top = 24),
                    Ladder(foot = 14, top = 35),
                    Ladder(foot = 27, top = 48),
                    Ladder(foot = 42, top = 63),
                    Ladder(foot = 60, top = 82),
                    Ladder(foot = 73, top = 93)
                )
            )
            SnakeDifficulty.HARD -> Pair(
                listOf(
                    Snake(head = 99, tail = 63),
                    Snake(head = 92, tail = 73),
                    Snake(head = 83, tail = 59),
                    Snake(head = 75, tail = 55),
                    Snake(head = 66, tail = 46),
                    Snake(head = 52, tail = 29),
                    Snake(head = 44, tail = 23),
                    Snake(head = 31, tail = 11)
                ),
                listOf(
                    Ladder(foot = 3, top = 22),
                    Ladder(foot = 15, top = 37),
                    Ladder(foot = 33, top = 65),
                    Ladder(foot = 56, top = 77),
                    Ladder(foot = 74, top = 94)
                )
            )
            SnakeDifficulty.VERY_HARD -> Pair(
                listOf(
                    Snake(head = 99, tail = 41),
                    Snake(head = 95, tail = 72),
                    Snake(head = 89, tail = 67),
                    Snake(head = 84, tail = 58),
                    Snake(head = 77, tail = 43),
                    Snake(head = 69, tail = 33),
                    Snake(head = 62, tail = 39),
                    Snake(head = 53, tail = 20),
                    Snake(head = 47, tail = 19),
                    Snake(head = 38, tail = 16),
                    Snake(head = 25, tail = 6)
                ),
                listOf(
                    Ladder(foot = 8, top = 26),
                    Ladder(foot = 22, top = 59),
                    Ladder(foot = 49, top = 70),
                    Ladder(foot = 68, top = 87)
                )
            )
        }
    }
}
