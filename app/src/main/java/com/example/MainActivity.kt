package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.GamePreferences
import com.example.data.LevelSelectTarget
import com.example.data.PuzzleDifficulty
import com.example.ui.hub.HubScreen
import com.example.ui.hub.LevelSelectScreen
import com.example.ui.photopuzzle.PhotoPuzzleScreen
import com.example.ui.photopuzzle.PhotoPuzzleViewModel
import com.example.ui.snakeludo.SnakeLudoScreen
import com.example.ui.snakeludo.SnakeLudoViewModel
import com.example.ui.theme.DarkBgStart
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.wordmaster.AnagramBlitzScreen
import com.example.ui.wordmaster.WordMasterScreen
import com.example.ui.wordmaster.WordMasterViewModel
import com.example.util.AudioHapticsManager

object AppRoutes {
    const val HOME = "home"
    const val PHOTO_PUZZLE = "photo_puzzle/{level}/{difficulty}?customUri={customUri}"
    const val WORD_MASTER = "word_master/{level}"
    const val ANAGRAM_BLITZ = "anagram_blitz"
    const val LEVEL_SELECT = "level_select/{target}"
    const val SNAKE_LUDO = "snake_ludo"

    fun photoPuzzle(level: Int, difficulty: PuzzleDifficulty, customUri: String? = null): String {
        val encodedUri = if (!customUri.isNullOrEmpty()) Uri.encode(customUri) else ""
        return "photo_puzzle/$level/${difficulty.name}?customUri=$encodedUri"
    }

    fun wordMaster(level: Int): String = "word_master/$level"

    fun levelSelect(target: LevelSelectTarget): String = "level_select/${target.name}"
}

class MainActivity : ComponentActivity() {

    private lateinit var preferences: GamePreferences
    private lateinit var audioHapticsManager: AudioHapticsManager
    private lateinit var photoViewModel: PhotoPuzzleViewModel
    private lateinit var wordViewModel: WordMasterViewModel
    private lateinit var snakeViewModel: SnakeLudoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferences = GamePreferences(applicationContext)
        audioHapticsManager = AudioHapticsManager(applicationContext)
        audioHapticsManager.soundEnabled = preferences.soundEnabled
        audioHapticsManager.hapticsEnabled = preferences.hapticsEnabled

        photoViewModel = PhotoPuzzleViewModel(application, preferences, audioHapticsManager)
        wordViewModel = WordMasterViewModel(application, preferences, audioHapticsManager)
        snakeViewModel = SnakeLudoViewModel(application, preferences, audioHapticsManager)

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBgStart),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    containerColor = DarkBgStart
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppRoutes.HOME,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Home Menu Screen
                        composable(AppRoutes.HOME) {
                            HubScreen(
                                preferences = preferences,
                                audioHaptics = audioHapticsManager,
                                onStartPhotoPuzzle = { lvl, diff, uri ->
                                    navController.navigate(AppRoutes.photoPuzzle(lvl, diff, uri))
                                },
                                onStartWordMaster = { lvl ->
                                    navController.navigate(AppRoutes.wordMaster(lvl))
                                },
                                onStartAnagramBlitz = {
                                    navController.navigate(AppRoutes.ANAGRAM_BLITZ)
                                },
                                onStartSnakeLudo = {
                                    navController.navigate(AppRoutes.SNAKE_LUDO)
                                },
                                onOpenLevelSelect = { target ->
                                    navController.navigate(AppRoutes.levelSelect(target))
                                }
                            )
                        }

                        // Mode 1: Photo Puzzle Board
                        composable(
                            route = AppRoutes.PHOTO_PUZZLE,
                            arguments = listOf(
                                navArgument("level") { type = NavType.IntType; defaultValue = 1 },
                                navArgument("difficulty") { type = NavType.StringType; defaultValue = PuzzleDifficulty.EASY.name },
                                navArgument("customUri") { type = NavType.StringType; nullable = true; defaultValue = null }
                            )
                        ) { backStackEntry ->
                            val level = backStackEntry.arguments?.getInt("level") ?: 1
                            val diffName = backStackEntry.arguments?.getString("difficulty") ?: PuzzleDifficulty.EASY.name
                            val difficulty = try {
                                PuzzleDifficulty.valueOf(diffName)
                            } catch (_: Exception) {
                                PuzzleDifficulty.EASY
                            }
                            val rawCustomUri = backStackEntry.arguments?.getString("customUri")
                            val customUri = if (!rawCustomUri.isNullOrEmpty()) Uri.decode(rawCustomUri) else null

                            PhotoPuzzleScreen(
                                viewModel = photoViewModel,
                                preferences = preferences,
                                levelNumber = level,
                                difficulty = difficulty,
                                customUri = customUri,
                                onBackToHub = { navController.popBackStack() }
                            )
                        }

                        // Mode 2: English Word Master
                        composable(
                            route = AppRoutes.WORD_MASTER,
                            arguments = listOf(
                                navArgument("level") { type = NavType.IntType; defaultValue = 1 }
                            )
                        ) { backStackEntry ->
                            val level = backStackEntry.arguments?.getInt("level") ?: 1

                            WordMasterScreen(
                                viewModel = wordViewModel,
                                preferences = preferences,
                                levelNumber = level,
                                onBackToHub = { navController.popBackStack() }
                            )
                        }

                        // Mode 3: Snake Ludo (Saanp Seedhi)
                        composable(AppRoutes.SNAKE_LUDO) {
                            SnakeLudoScreen(
                                viewModel = snakeViewModel,
                                preferences = preferences,
                                onBackToHub = { navController.popBackStack() }
                            )
                        }

                        // Anagram Blitz Mini-Mode
                        composable(AppRoutes.ANAGRAM_BLITZ) {
                            AnagramBlitzScreen(
                                preferences = preferences,
                                audioHaptics = audioHapticsManager,
                                onBackToHub = { navController.popBackStack() }
                            )
                        }

                        // Level Select
                        composable(
                            route = AppRoutes.LEVEL_SELECT,
                            arguments = listOf(
                                navArgument("target") { type = NavType.StringType; defaultValue = LevelSelectTarget.PHOTO_EASY.name }
                            )
                        ) { backStackEntry ->
                            val targetName = backStackEntry.arguments?.getString("target") ?: LevelSelectTarget.PHOTO_EASY.name
                            val target = try {
                                LevelSelectTarget.valueOf(targetName)
                            } catch (_: Exception) {
                                LevelSelectTarget.PHOTO_EASY
                            }

                            LevelSelectScreen(
                                targetMode = target,
                                preferences = preferences,
                                onSelectLevel = { lvl, diff ->
                                    navController.navigate(AppRoutes.photoPuzzle(lvl, diff, null))
                                },
                                onSelectWordLevel = { lvl ->
                                    navController.navigate(AppRoutes.wordMaster(lvl))
                                },
                                onBackToHub = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::audioHapticsManager.isInitialized) {
            audioHapticsManager.release()
        }
    }
}
