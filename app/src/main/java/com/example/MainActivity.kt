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
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.GamePreferences
import com.example.data.LevelSelectTarget
import com.example.data.PuzzleCategory
import com.example.data.PuzzleDifficulty
import com.example.ui.hub.HubScreen
import com.example.ui.hub.LevelSelectScreen
import com.example.ui.photopuzzle.PhotoPuzzleScreen
import com.example.ui.photopuzzle.PhotoPuzzleViewModel
import com.example.ui.theme.DarkBgStart
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AudioHapticsManager

object AppRoutes {
    const val HOME = "home"
    const val PHOTO_PUZZLE = "photo_puzzle/{level}/{difficulty}?category={category}&customUri={customUri}"
    const val LEVEL_SELECT = "level_select/{target}"

    fun photoPuzzle(
        level: Int,
        difficulty: PuzzleDifficulty,
        customUri: String? = null,
        category: PuzzleCategory? = null
    ): String {
        val encodedUri = if (!customUri.isNullOrEmpty()) Uri.encode(customUri) else ""
        val catName = category?.name ?: ""
        return "photo_puzzle/$level/${difficulty.name}?category=$catName&customUri=$encodedUri"
    }

    fun levelSelect(target: LevelSelectTarget): String = "level_select/${target.name}"
}

class MainActivity : ComponentActivity() {

    private lateinit var preferences: GamePreferences
    private lateinit var audioHapticsManager: AudioHapticsManager
    private lateinit var photoViewModel: PhotoPuzzleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferences = GamePreferences(applicationContext)
        audioHapticsManager = AudioHapticsManager(applicationContext)
        audioHapticsManager.soundEnabled = preferences.soundEnabled
        audioHapticsManager.hapticsEnabled = preferences.hapticsEnabled

        photoViewModel = PhotoPuzzleViewModel(application, preferences, audioHapticsManager)

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
                        // Home Menu Screen (Photo Jigsaw Studio)
                        composable(AppRoutes.HOME) {
                            HubScreen(
                                preferences = preferences,
                                audioHaptics = audioHapticsManager,
                                onStartPhotoPuzzle = { lvl, diff, uri, cat ->
                                    navController.navigate(AppRoutes.photoPuzzle(lvl, diff, uri, cat))
                                },
                                onOpenLevelSelect = { target ->
                                    navController.navigate(AppRoutes.levelSelect(target))
                                }
                            )
                        }

                        // Photo Puzzle Board
                        composable(
                            route = AppRoutes.PHOTO_PUZZLE,
                            arguments = listOf(
                                navArgument("level") { type = NavType.IntType; defaultValue = 1 },
                                navArgument("difficulty") { type = NavType.StringType; defaultValue = PuzzleDifficulty.EASY.name },
                                navArgument("category") { type = NavType.StringType; nullable = true; defaultValue = null },
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
                            val catName = backStackEntry.arguments?.getString("category")
                            val category = if (!catName.isNullOrEmpty()) {
                                try {
                                    PuzzleCategory.valueOf(catName)
                                } catch (_: Exception) {
                                    null
                                }
                            } else null

                            val rawCustomUri = backStackEntry.arguments?.getString("customUri")
                            val customUri = if (!rawCustomUri.isNullOrEmpty()) Uri.decode(rawCustomUri) else null

                            PhotoPuzzleScreen(
                                viewModel = photoViewModel,
                                preferences = preferences,
                                levelNumber = level,
                                difficulty = difficulty,
                                customUri = customUri,
                                category = category,
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
                                onSelectLevel = { lvl, diff, cat ->
                                    navController.navigate(AppRoutes.photoPuzzle(lvl, diff, null, cat))
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
