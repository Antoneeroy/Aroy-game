package com.example.ui.snakeludo

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GamePreferences
import com.example.util.AudioHapticsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SnakeLudoUiState(
    val difficulty: SnakeDifficulty = SnakeDifficulty.MIDDLE,
    val theme: SnakeTheme = SnakeTheme.CLASSIC_RETRO,
    val useArtBoard: Boolean = true,
    val playerMode: PlayerMode = PlayerMode.SOLO_VS_AI,
    val players: List<LudoPlayer> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val isRolling: Boolean = false,
    val diceValue: Int = 1,
    val snakes: List<Snake> = emptyList(),
    val ladders: List<Ladder> = emptyList(),
    val gameMessage: String = "Tap the dice to roll!",
    val isGameOver: Boolean = false,
    val podium: List<LudoPlayer> = emptyList(),
    val earnedCoins: Int = 0
) {
    val activePlayer: LudoPlayer?
        get() = players.getOrNull(currentPlayerIndex)
}

class SnakeLudoViewModel(
    application: Application,
    private val preferences: GamePreferences,
    private val audioHaptics: AudioHapticsManager
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SnakeLudoUiState())
    val uiState: StateFlow<SnakeLudoUiState> = _uiState.asStateFlow()

    private var aiJob: Job? = null
    private var consecutiveSixes: Int = 0

    init {
        val (snakes, ladders) = BoardConfigGenerator.getSnakesAndLadders(SnakeDifficulty.MIDDLE)
        val initialPlayers = createPlayers(PlayerMode.SOLO_VS_AI)
        _uiState.update {
            it.copy(
                snakes = snakes,
                ladders = ladders,
                players = initialPlayers,
                currentPlayerIndex = 0,
                gameMessage = "${initialPlayers[0].name}'s Turn (🔴) • Tap dice to roll!"
            )
        }
    }

    private fun createPlayers(mode: PlayerMode): List<LudoPlayer> {
        val p1 = LudoPlayer(0, "Red Pawn (You)", Color(0xFFE53935), "🔴", pos = 1, isAi = false)
        val p2 = if (mode == PlayerMode.SOLO_VS_AI) {
            LudoPlayer(1, "Blue Pawn (Robot AI)", Color(0xFF1E88E5), "🤖", pos = 1, isAi = true)
        } else {
            LudoPlayer(1, "Blue Pawn (Colleague 2)", Color(0xFF1E88E5), "🔵", pos = 1, isAi = false)
        }
        val p3 = LudoPlayer(2, "Yellow Pawn (Colleague 3)", Color(0xFFFACC15), "🟡", pos = 1, isAi = false)
        val p4 = LudoPlayer(3, "Green Pawn (Colleague 4)", Color(0xFF22C55E), "🟢", pos = 1, isAi = false)

        return when (mode) {
            PlayerMode.SOLO_VS_AI -> listOf(p1, p2)
            PlayerMode.TWO_PLAYERS -> listOf(p1, p2)
            PlayerMode.THREE_PLAYERS -> listOf(p1, p2, p3)
            PlayerMode.FOUR_PLAYERS -> listOf(p1, p2, p3, p4)
        }
    }

    fun setPlayerMode(mode: PlayerMode) {
        aiJob?.cancel()
        consecutiveSixes = 0
        val newPlayers = createPlayers(mode)
        _uiState.update {
            it.copy(
                playerMode = mode,
                players = newPlayers,
                currentPlayerIndex = 0,
                isRolling = false,
                isGameOver = false,
                podium = emptyList(),
                earnedCoins = 0,
                gameMessage = "${newPlayers[0].name}'s Turn (${newPlayers[0].avatarIcon}) • Tap to roll!"
            )
        }
    }

    fun setDifficulty(diff: SnakeDifficulty) {
        aiJob?.cancel()
        consecutiveSixes = 0
        val (snakes, ladders) = BoardConfigGenerator.getSnakesAndLadders(diff)
        val resetPlayers = _uiState.value.players.map { it.copy(pos = 1, rank = null) }
        _uiState.update {
            it.copy(
                difficulty = diff,
                snakes = snakes,
                ladders = ladders,
                players = resetPlayers,
                currentPlayerIndex = 0,
                isRolling = false,
                isGameOver = false,
                podium = emptyList(),
                earnedCoins = 0,
                gameMessage = "Difficulty set to ${diff.displayName}! ${resetPlayers[0].name} rolls first."
            )
        }
    }

    fun setTheme(theme: SnakeTheme) {
        audioHaptics.vibrateWordLetter()
        val shouldEnableArt = (theme.boardDrawableRes != null)
        _uiState.update { it.copy(theme = theme, useArtBoard = shouldEnableArt) }
    }

    fun toggleArtBoard() {
        audioHaptics.vibrateWordLetter()
        _uiState.update { it.copy(useArtBoard = !it.useArtBoard) }
    }

    fun rollDice() {
        val state = _uiState.value
        val active = state.activePlayer ?: return
        if (state.isRolling || state.isGameOver || active.rank != null || active.isAi) return

        executePlayerRoll(active)
    }

    private fun executePlayerRoll(player: LudoPlayer) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRolling = true, gameMessage = "${player.name} is rolling dice...") }

            // Dice rolling animation
            for (i in 0..6) {
                audioHaptics.playWordConnectTone()
                audioHaptics.vibrateWordLetter()
                _uiState.update { it.copy(diceValue = Random.nextInt(1, 7)) }
                delay(60)
            }

            val finalRoll = Random.nextInt(1, 7)
            _uiState.update { it.copy(diceValue = finalRoll, isRolling = false) }

            // Movement logic
            val currentPos = player.pos
            val targetPos = currentPos + finalRoll

            if (targetPos > 100) {
                _uiState.update {
                    it.copy(gameMessage = "${player.name} rolled $finalRoll! Needs exact roll to reach 100.")
                }
                delay(1000)
                advanceTurn(player, rolledSix = false)
                return@launch
            }

            // Step-by-step jump animation
            for (step in (currentPos + 1)..targetPos) {
                updatePlayerPos(player.id, step)
                audioHaptics.playSnapSound()
                delay(170)
            }

            // Check if reached 100
            if (targetPos == 100) {
                handlePlayerFinish(player)
                return@launch
            }

            // Check Snake or Ladder
            val finalTile = handleSnakeOrLadder(player, targetPos)

            // Check if reached 100 via ladder
            if (finalTile == 100) {
                handlePlayerFinish(player)
                return@launch
            }

            // Extra turn on rolling 6
            val isSix = (finalRoll == 6)
            if (isSix) {
                consecutiveSixes++
                if (consecutiveSixes >= 3) {
                    _uiState.update {
                        it.copy(gameMessage = "3 consecutive 6s! Turn forfeited.")
                    }
                    consecutiveSixes = 0
                    delay(1000)
                    advanceTurn(player, rolledSix = false)
                } else {
                    _uiState.update {
                        it.copy(gameMessage = "🎉 Rolled a 6! ${player.name} gets an extra turn!")
                    }
                    if (player.isAi) {
                        delay(900)
                        triggerAiTurn()
                    }
                }
            } else {
                consecutiveSixes = 0
                delay(600)
                advanceTurn(player, rolledSix = false)
            }
        }
    }

    private suspend fun handleSnakeOrLadder(player: LudoPlayer, pos: Int): Int {
        val state = _uiState.value
        val snake = state.snakes.firstOrNull { it.head == pos }
        val ladder = state.ladders.firstOrNull { it.foot == pos }

        return when {
            snake != null -> {
                audioHaptics.playErrorBuzz()
                audioHaptics.vibrateError()
                _uiState.update {
                    it.copy(gameMessage = "🐍 Bitten by Snake at ${snake.head}! ${player.name} slid down to ${snake.tail}!")
                }
                delay(500)
                updatePlayerPos(player.id, snake.tail)
                delay(800)
                snake.tail
            }
            ladder != null -> {
                audioHaptics.playHintChime()
                audioHaptics.vibrateSuccess()
                _uiState.update {
                    it.copy(gameMessage = "🪜 Climbed a Ladder from ${ladder.foot} to ${ladder.top}! 🎉")
                }
                delay(500)
                updatePlayerPos(player.id, ladder.top)
                delay(800)
                ladder.top
            }
            else -> pos
        }
    }

    private fun updatePlayerPos(playerId: Int, newPos: Int) {
        _uiState.update { state ->
            val updated = state.players.map {
                if (it.id == playerId) it.copy(pos = newPos) else it
            }
            state.copy(players = updated)
        }
    }

    private fun handlePlayerFinish(player: LudoPlayer) {
        val currentFinished = _uiState.value.players.count { it.rank != null }
        val assignedRank = currentFinished + 1

        val updatedPlayers = _uiState.value.players.map {
            if (it.id == player.id) it.copy(pos = 100, rank = assignedRank) else it
        }

        val updatedPodium = _uiState.value.podium + player.copy(pos = 100, rank = assignedRank)

        val coinsAwarded = when (assignedRank) {
            1 -> _uiState.value.difficulty.coinReward
            2 -> (_uiState.value.difficulty.coinReward * 0.6f).toInt()
            3 -> (_uiState.value.difficulty.coinReward * 0.3f).toInt()
            else -> 10
        }

        if (!player.isAi) {
            preferences.addCoins(coinsAwarded)
        }

        audioHaptics.playVictoryFanfare()
        audioHaptics.vibrateSuccess()

        // Check if game is over (all or all-but-one finished, or 1P finished in vs AI)
        val remainingActive = updatedPlayers.filter { it.rank == null }
        val isOver = if (_uiState.value.playerMode == PlayerMode.SOLO_VS_AI) {
            true // Game ends when either player or AI reaches 100
        } else {
            remainingActive.size <= 1
        }

        _uiState.update {
            it.copy(
                players = updatedPlayers,
                podium = updatedPodium,
                isGameOver = isOver,
                earnedCoins = if (!player.isAi) coinsAwarded else 0,
                gameMessage = "🏆 ${player.name} finished Rank #$assignedRank! 🌟"
            )
        }

        if (!isOver) {
            advanceTurn(player, rolledSix = false)
        }
    }

    private fun advanceTurn(justPlayed: LudoPlayer, rolledSix: Boolean) {
        if (_uiState.value.isGameOver) return

        val state = _uiState.value
        val totalPlayers = state.players.size
        var nextIdx = (state.currentPlayerIndex + 1) % totalPlayers

        // Skip players who have already finished
        var attempts = 0
        while (state.players[nextIdx].rank != null && attempts < totalPlayers) {
            nextIdx = (nextIdx + 1) % totalPlayers
            attempts++
        }

        val nextPlayer = state.players[nextIdx]
        _uiState.update {
            it.copy(
                currentPlayerIndex = nextIdx,
                gameMessage = "${nextPlayer.name}'s Turn (${nextPlayer.avatarIcon}) • Roll dice!"
            )
        }

        if (nextPlayer.isAi && !state.isGameOver) {
            triggerAiTurn()
        }
    }

    private fun triggerAiTurn() {
        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            delay(750)
            val state = _uiState.value
            val active = state.activePlayer
            if (active == null || !active.isAi || state.isGameOver) return@launch

            _uiState.update { it.copy(isRolling = true, gameMessage = "${active.name} is rolling dice...") }

            for (i in 0..5) {
                _uiState.update { it.copy(diceValue = Random.nextInt(1, 7)) }
                delay(60)
            }

            val finalRoll = Random.nextInt(1, 7)
            _uiState.update { it.copy(diceValue = finalRoll, isRolling = false) }

            val currentPos = active.pos
            val targetPos = currentPos + finalRoll

            if (targetPos > 100) {
                _uiState.update {
                    it.copy(gameMessage = "${active.name} rolled $finalRoll (Over 100, turn passed!)")
                }
                delay(1000)
                advanceTurn(active, rolledSix = false)
                return@launch
            }

            for (step in (currentPos + 1)..targetPos) {
                updatePlayerPos(active.id, step)
                delay(170)
            }

            if (targetPos == 100) {
                handlePlayerFinish(active)
                return@launch
            }

            val finalTile = handleSnakeOrLadder(active, targetPos)
            if (finalTile == 100) {
                handlePlayerFinish(active)
                return@launch
            }

            if (finalRoll == 6) {
                _uiState.update {
                    it.copy(gameMessage = "🤖 AI rolled a 6! AI rolls again.")
                }
                delay(900)
                triggerAiTurn()
            } else {
                delay(600)
                advanceTurn(active, rolledSix = false)
            }
        }
    }

    fun restartGame() {
        aiJob?.cancel()
        consecutiveSixes = 0
        val (snakes, ladders) = BoardConfigGenerator.getSnakesAndLadders(_uiState.value.difficulty)
        val resetPlayers = createPlayers(_uiState.value.playerMode)
        _uiState.update {
            it.copy(
                players = resetPlayers,
                currentPlayerIndex = 0,
                isRolling = false,
                isGameOver = false,
                podium = emptyList(),
                earnedCoins = 0,
                snakes = snakes,
                ladders = ladders,
                gameMessage = "New Match! ${resetPlayers[0].name} rolls first."
            )
        }
    }
}
