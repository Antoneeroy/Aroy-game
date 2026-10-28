package com.example.ui.photopuzzle

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GamePreferences
import com.example.data.PhotoPuzzleLevelConfig
import com.example.data.PuzzleCategory
import com.example.data.PuzzleDifficulty
import com.example.data.PuzzlePiece
import com.example.util.AudioHapticsManager
import com.example.util.ImageSlicer
import com.example.util.ProceduralImageGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

data class PhotoPuzzleUiState(
    val levelNumber: Int = 1,
    val difficulty: PuzzleDifficulty = PuzzleDifficulty.EASY,
    val category: PuzzleCategory = PuzzleCategory.ANIMALS,
    val pieces: List<PuzzlePiece> = emptyList(),
    val boardSlots: Map<Int, PuzzlePiece> = emptyMap(), // slotIndex -> placed piece
    val selectedPiece: PuzzlePiece? = null,
    val ghostOpacity: Float = 0.35f,
    val edgeOnlyFilter: Boolean = false,
    val remainingTimeSec: Int = 0,
    val isTimerFrozen: Boolean = false,
    val elapsedSec: Int = 0,
    val isCompleted: Boolean = false,
    val earnedStars: Int = 0,
    val earnedCoins: Int = 0,
    val isLoadingImage: Boolean = true,
    val fullBitmap: Bitmap? = null,
    val justSnappedSlot: Int? = null,
    val customImageUri: String? = null,
    val slotTemplates: Map<Int, PuzzlePiece> = emptyMap()
)

class PhotoPuzzleViewModel(
    application: Application,
    private val preferences: GamePreferences,
    private val audioHaptics: AudioHapticsManager
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PhotoPuzzleUiState())
    val uiState: StateFlow<PhotoPuzzleUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startLevel(level: Int, difficulty: PuzzleDifficulty, customUri: String? = null) {
        timerJob?.cancel()
        val category = PuzzleCategory.forLevel(level)
        _uiState.update {
            it.copy(
                levelNumber = level,
                difficulty = difficulty,
                category = category,
                pieces = emptyList(),
                boardSlots = emptyMap(),
                selectedPiece = null,
                slotTemplates = emptyMap(),
                ghostOpacity = difficulty.ghostOpacityDefault,
                edgeOnlyFilter = false,
                remainingTimeSec = difficulty.defaultTimerSec,
                isTimerFrozen = false,
                elapsedSec = 0,
                isCompleted = false,
                isLoadingImage = true,
                fullBitmap = null,
                justSnappedSlot = null,
                customImageUri = customUri
            )
        }

        viewModelScope.launch {
            val bitmap = loadOrGenerateBitmap(level, category, customUri)
            val pieces = withContext(Dispatchers.Default) {
                ImageSlicer.sliceImage(bitmap, difficulty)
            }
            val slotTemplates = pieces.associateBy { it.correctIndex }
            _uiState.update {
                it.copy(
                    fullBitmap = bitmap,
                    pieces = pieces,
                    slotTemplates = slotTemplates,
                    isLoadingImage = false
                )
            }
            startTimer()
        }
    }

    private suspend fun loadOrGenerateBitmap(level: Int, category: PuzzleCategory, customUri: String?): Bitmap =
        withContext(Dispatchers.IO) {
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.ARGB_8888
                inDither = true
            }

            // If custom photo chosen from gallery ("Play Your Own Photo")
            if (!customUri.isNullOrEmpty()) {
                try {
                    val uri = Uri.parse(customUri)
                    val input = getApplication<Application>().contentResolver.openInputStream(uri)
                    val rawBm = BitmapFactory.decodeStream(input, null, options)
                    if (rawBm != null) {
                        // High quality center-crop to 1:1 square so no aspect distortion
                        val side = minOf(rawBm.width, rawBm.height)
                        val x = (rawBm.width - side) / 2
                        val y = (rawBm.height - side) / 2
                        val squareBm = Bitmap.createBitmap(rawBm, x, y, side, side)
                        val hdCleanBm = Bitmap.createScaledBitmap(squareBm, 1024, 1024, true)
                        return@withContext hdCleanBm
                    }
                } catch (_: Exception) {
                }
            }

            // Try dynamic internet HD seed image
            try {
                val imageUrl = category.getImageUrl(level)
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 3500
                connection.readTimeout = 4500
                connection.connect()
                if (connection.responseCode == 200) {
                    val stream = connection.inputStream
                    val netBm = BitmapFactory.decodeStream(stream, null, options)
                    if (netBm != null) {
                        val side = minOf(netBm.width, netBm.height)
                        val x = (netBm.width - side) / 2
                        val y = (netBm.height - side) / 2
                        val squareBm = Bitmap.createBitmap(netBm, x, y, side, side)
                        val hdCleanBm = Bitmap.createScaledBitmap(squareBm, 1024, 1024, true)
                        return@withContext hdCleanBm
                    }
                }
            } catch (_: Exception) {
                // Network unavailable or timeout; fall back seamlessly
            }

            // Fallback to high-definition procedural rendering
            ProceduralImageGenerator.generateCategoryBitmap(category, level, 1024, 1024)
        }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val state = _uiState.value
                if (state.isCompleted) break

                val newElapsed = state.elapsedSec + 1
                val newRemaining = if (state.difficulty.defaultTimerSec > 0 && !state.isTimerFrozen) {
                    (state.remainingTimeSec - 1).coerceAtLeast(0)
                } else {
                    state.remainingTimeSec
                }

                _uiState.update {
                    it.copy(elapsedSec = newElapsed, remainingTimeSec = newRemaining)
                }
            }
        }
    }

    fun selectPiece(piece: PuzzlePiece) {
        if (_uiState.value.isCompleted) return
        audioHaptics.vibrateWordLetter()
        _uiState.update {
            it.copy(selectedPiece = if (it.selectedPiece?.id == piece.id) null else piece)
        }
    }

    fun rotateSelectedPiece() {
        val selected = _uiState.value.selectedPiece ?: return
        val newRot = (selected.rotation + 90) % 360
        val rotatedPiece = selected.copy(
            rotation = newRot,
            bitmap = ImageSlicer.rotateBitmap(selected.bitmap, 90f)
        )
        audioHaptics.playWordConnectTone()
        audioHaptics.vibrateWordLetter()
        _uiState.update { state ->
            state.copy(
                selectedPiece = rotatedPiece,
                pieces = state.pieces.map { if (it.id == rotatedPiece.id) rotatedPiece else it }
            )
        }
    }

    fun onSlotClicked(slotIndex: Int) {
        val state = _uiState.value
        if (state.isCompleted) return
        val selected = state.selectedPiece ?: return

        // Check if correct placement (slotIndex matches correctIndex AND rotation is 0)
        if (selected.correctIndex == slotIndex && selected.rotation == 0) {
            // Magnetic Snap!
            audioHaptics.playSnapSound()
            audioHaptics.vibrateSnap()

            val updatedSlots = state.boardSlots + (slotIndex to selected)
            val updatedPieces = state.pieces.filterNot { it.id == selected.id }

            _uiState.update {
                it.copy(
                    boardSlots = updatedSlots,
                    pieces = updatedPieces,
                    selectedPiece = null,
                    justSnappedSlot = slotIndex
                )
            }

            // Check if level finished
            val totalPieces = state.difficulty.totalPieces
            if (updatedSlots.size == totalPieces) {
                onLevelWon()
            }
        } else {
            // Incorrect position or piece not rotated correctly
            audioHaptics.playErrorBuzz()
            audioHaptics.vibrateError()
        }
    }

    private fun onLevelWon() {
        val state = _uiState.value
        timerJob?.cancel()
        audioHaptics.playVictoryFanfare()
        audioHaptics.vibrateSuccess()

        val stars = when {
            state.elapsedSec <= 45 -> 3
            state.elapsedSec <= 120 -> 2
            else -> 1
        }
        val coinsReward = when (stars) {
            3 -> 50
            2 -> 35
            else -> 20
        }

        preferences.addCoins(coinsReward)
        preferences.setPhotoStars(state.difficulty.name, state.levelNumber, stars)
        preferences.unlockNextPhotoLevel(state.difficulty.name, state.levelNumber)
        preferences.recordPhotoSolved(state.elapsedSec)

        _uiState.update {
            it.copy(
                isCompleted = true,
                earnedStars = stars,
                earnedCoins = coinsReward,
                selectedPiece = null
            )
        }
    }

    fun applyMagnetHint(): Boolean {
        val state = _uiState.value
        if (state.isCompleted) return false
        if (!preferences.spendCoins(30)) return false // Costs 30 coins

        // Find first missing piece
        val missingPiece = state.pieces.firstOrNull() ?: return false
        val targetSlot = missingPiece.correctIndex

        audioHaptics.playHintChime()
        audioHaptics.vibrateSnap()

        // Normalize rotation if needed
        val readyPiece = if (missingPiece.rotation != 0) {
            missingPiece.copy(
                rotation = 0,
                bitmap = ImageSlicer.rotateBitmap(missingPiece.bitmap, (360 - missingPiece.rotation).toFloat())
            )
        } else missingPiece

        val updatedSlots = state.boardSlots + (targetSlot to readyPiece)
        val updatedPieces = state.pieces.filterNot { it.id == missingPiece.id }

        _uiState.update {
            it.copy(
                boardSlots = updatedSlots,
                pieces = updatedPieces,
                selectedPiece = null,
                justSnappedSlot = targetSlot
            )
        }

        if (updatedSlots.size == state.difficulty.totalPieces) {
            onLevelWon()
        }
        return true
    }

    fun applyTimeFreeze(): Boolean {
        val state = _uiState.value
        if (state.isTimerFrozen || state.difficulty.defaultTimerSec <= 0) return false
        if (!preferences.spendCoins(25)) return false // Costs 25 coins

        audioHaptics.playHintChime()
        _uiState.update {
            it.copy(
                isTimerFrozen = true,
                remainingTimeSec = it.remainingTimeSec + 30
            )
        }
        return true
    }

    fun toggleEdgeFilter() {
        audioHaptics.vibrateWordLetter()
        _uiState.update {
            it.copy(edgeOnlyFilter = !it.edgeOnlyFilter)
        }
    }

    fun setGhostOpacity(opacity: Float) {
        _uiState.update {
            it.copy(ghostOpacity = opacity.coerceIn(0f, 0.5f))
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
