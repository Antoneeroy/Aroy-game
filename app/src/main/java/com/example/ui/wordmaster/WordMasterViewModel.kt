package com.example.ui.wordmaster

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GamePreferences
import com.example.data.WordDefinition
import com.example.data.WordLevelData
import com.example.data.WordMasterLevelRepository
import com.example.util.AudioHapticsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WordMasterUiState(
    val levelNumber: Int = 1,
    val levelData: WordLevelData = WordMasterLevelRepository.getLevel(1),
    val currentLetters: List<Char> = emptyList(),
    val foundWords: Set<String> = emptySet(),
    val revealedHintLetters: Map<String, Set<Int>> = emptyMap(), // word -> set of revealed indices
    val currentWordInput: String = "",
    val feedbackMessage: String? = null,
    val isCompleted: Boolean = false,
    val elapsedSec: Int = 0,
    val earnedStars: Int = 0,
    val earnedCoins: Int = 0,
    val selectedDefinition: WordDefinition? = null,
    val bonusJarCount: Int = 0,
    val blurFraction: Float = 1.0f // 1.0 = full blur, 0.0 = crystal clear picture clue
)

class WordMasterViewModel(
    application: Application,
    private val preferences: GamePreferences,
    private val audioHaptics: AudioHapticsManager
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WordMasterUiState())
    val uiState: StateFlow<WordMasterUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startLevel(level: Int) {
        timerJob?.cancel()
        val levelData = WordMasterLevelRepository.getLevel(level)
        _uiState.update {
            it.copy(
                levelNumber = level,
                levelData = levelData,
                currentLetters = levelData.letters.shuffled(),
                foundWords = emptySet(),
                revealedHintLetters = emptyMap(),
                currentWordInput = "",
                feedbackMessage = null,
                isCompleted = false,
                elapsedSec = 0,
                selectedDefinition = null,
                bonusJarCount = preferences.bonusWordsCount,
                blurFraction = 1.0f
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_uiState.value.isCompleted) break
                _uiState.update { it.copy(elapsedSec = it.elapsedSec + 1) }
            }
        }
    }

    fun shuffleLetters() {
        audioHaptics.vibrateWordLetter()
        _uiState.update {
            it.copy(currentLetters = it.currentLetters.shuffled())
        }
    }

    fun onLetterSelected() {
        audioHaptics.vibrateWordLetter()
    }

    fun submitWord(word: String) {
        val upper = word.uppercase()
        val state = _uiState.value
        val targets = state.levelData.targetWords
        val bonuses = state.levelData.bonusWords

        when {
            targets.contains(upper) -> {
                if (state.foundWords.contains(upper)) {
                    showFeedback("Already found!")
                    audioHaptics.playErrorBuzz()
                } else {
                    val updatedFound = state.foundWords + upper
                    audioHaptics.playWordConnectTone()
                    audioHaptics.vibrateSuccess()

                    val remaining = targets.size - updatedFound.size
                    val newBlur = if (targets.isNotEmpty()) {
                        remaining.toFloat() / targets.size.toFloat()
                    } else 0f

                    preferences.recordWordsFound(1)

                    _uiState.update {
                        it.copy(
                            foundWords = updatedFound,
                            feedbackMessage = "Found $upper!",
                            blurFraction = newBlur.coerceIn(0f, 1f)
                        )
                    }

                    if (updatedFound.size == targets.size) {
                        onLevelWon()
                    }
                }
            }
            bonuses.contains(upper) -> {
                val rewarded = preferences.addBonusWord()
                audioHaptics.playHintChime()
                audioHaptics.vibrateSnap()
                _uiState.update {
                    it.copy(
                        bonusJarCount = preferences.bonusWordsCount,
                        feedbackMessage = if (rewarded) "Bonus Jar Full! +50 Coins!" else "Bonus Word Added to Jar! (+1)"
                    )
                }
            }
            else -> {
                audioHaptics.playErrorBuzz()
                audioHaptics.vibrateError()
                showFeedback("Not in puzzle word list")
            }
        }
    }

    private fun showFeedback(msg: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(feedbackMessage = msg) }
            delay(1500)
            _uiState.update { if (it.feedbackMessage == msg) it.copy(feedbackMessage = null) else it }
        }
    }

    private fun onLevelWon() {
        timerJob?.cancel()
        audioHaptics.playVictoryFanfare()
        audioHaptics.vibrateSuccess()

        val state = _uiState.value
        val stars = when {
            state.elapsedSec <= 50 -> 3
            state.elapsedSec <= 110 -> 2
            else -> 1
        }
        val coins = when (stars) {
            3 -> 45
            2 -> 30
            else -> 15
        }

        preferences.addCoins(coins)
        preferences.setWordStars(state.levelNumber, stars)
        preferences.unlockNextWordLevel(state.levelNumber)

        _uiState.update {
            it.copy(
                isCompleted = true,
                earnedStars = stars,
                earnedCoins = coins,
                blurFraction = 0f
            )
        }
    }

    fun applyLetterBulbHint(): Boolean {
        val state = _uiState.value
        if (state.isCompleted) return false
        if (!preferences.spendCoins(25)) return false // Costs 25 coins

        // Find an unfound target word and reveal 1 character
        val unfound = state.levelData.targetWords.firstOrNull { !state.foundWords.contains(it) } ?: return false
        val currentRevealed = state.revealedHintLetters[unfound] ?: emptySet()
        val unrevealedIndex = (0 until unfound.length).firstOrNull { !currentRevealed.contains(it) } ?: return false

        audioHaptics.playHintChime()
        _uiState.update {
            val updatedMap = it.revealedHintLetters + (unfound to (currentRevealed + unrevealedIndex))
            it.copy(revealedHintLetters = updatedMap)
        }
        return true
    }

    fun openDefinition(word: String) {
        val definition = _uiState.value.levelData.definitions[word] ?: WordDefinition(
            word = word,
            partOfSpeech = "word",
            definition = "A valid English vocabulary word.",
            exampleSentence = "Expand your English vocabulary with $word."
        )
        _uiState.update { it.copy(selectedDefinition = definition) }
    }

    fun closeDefinition() {
        _uiState.update { it.copy(selectedDefinition = null) }
    }

    fun speakDefinitionWord() {
        val def = _uiState.value.selectedDefinition ?: return
        audioHaptics.speak(def.word)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
