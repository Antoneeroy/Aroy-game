package com.example.data

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GamePreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("aroy_puzzle_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_COINS = "key_coins"
        private const val KEY_PHOTO_LEVEL_EASY = "key_photo_level_easy"
        private const val KEY_PHOTO_LEVEL_MED = "key_photo_level_med"
        private const val KEY_PHOTO_LEVEL_HARD = "key_photo_level_hard"
        private const val KEY_WORD_LEVEL = "key_word_level"
        private const val KEY_BONUS_WORDS_COUNT = "key_bonus_words_count"
        private const val KEY_SOUND_ENABLED = "key_sound_enabled"
        private const val KEY_HAPTICS_ENABLED = "key_haptics_enabled"
        private const val KEY_STREAK = "key_streak"
        private const val KEY_LAST_DAILY_CLAIM = "key_last_daily_claim"
        private const val KEY_TOTAL_PHOTO_SOLVED = "key_total_photo_solved"
        private const val KEY_TOTAL_WORDS_FOUND = "key_total_words_found"
        private const val KEY_FASTEST_PHOTO_SEC = "key_fastest_photo_sec"
        private const val KEY_FASTEST_WORD_SEC = "key_fastest_word_sec"
    }

    var coins: Int
        get() = prefs.getInt(KEY_COINS, 250) // starts with generous 250 coins
        set(value) = prefs.edit().putInt(KEY_COINS, value.coerceAtLeast(0)).apply()

    fun addCoins(amount: Int) {
        coins += amount
    }

    fun spendCoins(amount: Int): Boolean {
        if (coins >= amount) {
            coins -= amount
            return true
        }
        return false
    }

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var hapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS_ENABLED, value).apply()

    // Photo levels unlocked (1 to 100 per difficulty)
    fun getPhotoUnlockedLevel(difficulty: String): Int {
        val key = when (difficulty.lowercase(Locale.ROOT)) {
            "easy" -> KEY_PHOTO_LEVEL_EASY
            "medium" -> KEY_PHOTO_LEVEL_MED
            "hard" -> KEY_PHOTO_LEVEL_HARD
            else -> KEY_PHOTO_LEVEL_EASY
        }
        return prefs.getInt(key, 1).coerceIn(1, 100)
    }

    fun unlockNextPhotoLevel(difficulty: String, currentLevel: Int) {
        val key = when (difficulty.lowercase(Locale.ROOT)) {
            "easy" -> KEY_PHOTO_LEVEL_EASY
            "medium" -> KEY_PHOTO_LEVEL_MED
            "hard" -> KEY_PHOTO_LEVEL_HARD
            else -> KEY_PHOTO_LEVEL_EASY
        }
        val currentUnlocked = prefs.getInt(key, 1)
        if (currentLevel >= currentUnlocked) {
            prefs.edit().putInt(key, (currentLevel + 1).coerceAtMost(100)).apply()
        }
    }

    // Word Master unlocked level (1 to 100)
    var wordMasterUnlockedLevel: Int
        get() = prefs.getInt(KEY_WORD_LEVEL, 1).coerceIn(1, 100)
        set(value) = prefs.edit().putInt(KEY_WORD_LEVEL, value.coerceIn(1, 100)).apply()

    fun unlockNextWordLevel(currentLevel: Int) {
        if (currentLevel >= wordMasterUnlockedLevel) {
            wordMasterUnlockedLevel = (currentLevel + 1).coerceAtMost(100)
        }
    }

    // Stars saved per level
    fun getPhotoStars(difficulty: String, level: Int): Int {
        return prefs.getInt("stars_photo_${difficulty}_$level", 0)
    }

    fun setPhotoStars(difficulty: String, level: Int, stars: Int) {
        val prev = getPhotoStars(difficulty, level)
        if (stars > prev) {
            prefs.edit().putInt("stars_photo_${difficulty}_$level", stars).apply()
        }
    }

    fun getWordStars(level: Int): Int {
        return prefs.getInt("stars_word_$level", 0)
    }

    fun setWordStars(level: Int, stars: Int) {
        val prev = getWordStars(level)
        if (stars > prev) {
            prefs.edit().putInt("stars_word_$level", stars).apply()
        }
    }

    // Bonus words jar
    var bonusWordsCount: Int
        get() = prefs.getInt(KEY_BONUS_WORDS_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_BONUS_WORDS_COUNT, value).apply()

    fun addBonusWord(): Boolean {
        val current = bonusWordsCount + 1
        if (current >= 10) {
            bonusWordsCount = 0
            addCoins(50)
            return true // Triggered 10-word bonus coin reward!
        } else {
            bonusWordsCount = current
            return false
        }
    }

    // Daily streak & reward
    val todayDateString: String
        get() = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

    var streakCount: Int
        get() = prefs.getInt(KEY_STREAK, 1)
        set(value) = prefs.edit().putInt(KEY_STREAK, value).apply()

    val canClaimDailyReward: Boolean
        get() {
            val lastClaim = prefs.getString(KEY_LAST_DAILY_CLAIM, "") ?: ""
            return lastClaim != todayDateString
        }

    fun claimDailyReward(): Int {
        if (!canClaimDailyReward) return 0
        streakCount += 1
        prefs.edit().putString(KEY_LAST_DAILY_CLAIM, todayDateString).apply()
        val reward = 100 + (streakCount * 10).coerceAtMost(100)
        addCoins(reward)
        return reward
    }

    // Lifetime Statistics
    var totalPhotoSolved: Int
        get() = prefs.getInt(KEY_TOTAL_PHOTO_SOLVED, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_PHOTO_SOLVED, value).apply()

    var totalWordsFound: Int
        get() = prefs.getInt(KEY_TOTAL_WORDS_FOUND, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_WORDS_FOUND, value).apply()

    var fastestPhotoSec: Int
        get() = prefs.getInt(KEY_FASTEST_PHOTO_SEC, 0)
        set(value) = prefs.edit().putInt(KEY_FASTEST_PHOTO_SEC, value).apply()

    fun recordPhotoSolved(elapsedSec: Int) {
        totalPhotoSolved += 1
        val best = fastestPhotoSec
        if (best == 0 || elapsedSec < best) {
            fastestPhotoSec = elapsedSec
        }
    }

    fun recordWordsFound(count: Int) {
        totalWordsFound += count
    }

    val brainIqScore: Int
        get() = (totalPhotoSolved * 25) + (totalWordsFound * 15) + (streakCount * 40) + 100

    val playerRank: String
        get() = when {
            brainIqScore >= 6000 -> "Grandmaster"
            brainIqScore >= 3000 -> "Master"
            brainIqScore >= 1500 -> "Scholar"
            brainIqScore >= 600 -> "Apprentice"
            else -> "Beginner"
        }
}
