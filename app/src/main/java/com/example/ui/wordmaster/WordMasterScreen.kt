package com.example.ui.wordmaster

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.GamePreferences
import com.example.ui.components.CoinBadge
import com.example.ui.components.DictionaryDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.LevelCompleteDialog
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GlowGradient
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordMasterScreen(
    viewModel: WordMasterViewModel,
    preferences: GamePreferences,
    levelNumber: Int,
    onBackToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(levelNumber) {
        viewModel.startLevel(levelNumber)
    }

    BackHandler {
        onBackToHub()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(max = 600.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToHub,
                    modifier = Modifier.testTag("word_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Level ${uiState.levelNumber}",
                        color = VioletAccent,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = uiState.levelData.categoryName,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                CoinBadge(
                    coins = preferences.coins,
                    modifier = Modifier.testTag("word_coins_badge")
                )
            }

            // Top Status Bar: Picture Clue Hybrid & Bonus Jar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(GlassSurface)
                    .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Picture Clue with progressive unblur!
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    val blurRadius = (uiState.blurFraction * 20f).dp
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0E172A))
                    ) {
                        AsyncImage(
                            model = uiState.levelData.clueImageUrl,
                            contentDescription = "Picture Clue",
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(blurRadius),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column {
                        Text(
                            text = if (uiState.blurFraction <= 0.05f) "Picture Unblurred! ✨" else "Picture Clue (${(100 - uiState.blurFraction * 100).toInt()}%)",
                            color = CyanAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.foundWords.size}/${uiState.levelData.targetWords.size} words solved",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Bonus Word Jar
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33F59E0B))
                        .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Jar: ${uiState.bonusJarCount}/10 🏺", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("+50¢ reward", color = TextMuted, fontSize = 9.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Crossword Word Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x550F172A))
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(16.dp))
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.levelData.targetWords.forEach { targetWord ->
                        val isFound = uiState.foundWords.contains(targetWord)
                        val revealedIndices = uiState.revealedHintLetters[targetWord] ?: emptySet()

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFound) Color(0xD9047857) else GlassSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isFound) EmeraldAccent else GlassBorderSubtle,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    if (isFound) {
                                        viewModel.openDefinition(targetWord)
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("crossword_word_$targetWord"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            targetWord.forEachIndexed { idx, char ->
                                val showChar = isFound || revealedIndices.contains(idx)
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isFound) Color(0x33FFFFFF) else Color(0x22000000)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (showChar) char.toString() else "",
                                        color = if (isFound) Color.White else GoldAccent,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            if (isFound) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Dictionary info",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Feedback Message Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                contentAlignment = Alignment.Center
            ) {
                val msg = uiState.feedbackMessage
                if (msg != null) {
                    Text(
                        text = msg,
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Controls Row (Letter Bulb Hint)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xD91E293B))
                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .clickable { viewModel.applyLetterBulbHint() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("letter_bulb_hint_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Text("Bulb Hint (25¢)", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Word Swipe Wheel
            WordWheel(
                letters = uiState.currentLetters,
                onWordCompleted = { word -> viewModel.submitWord(word) },
                onLetterSelected = { viewModel.onLetterSelected() },
                onShuffle = { viewModel.shuffleLetters() },
                wheelSize = 220.dp
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Instant Dictionary Sheet Dialog
        uiState.selectedDefinition?.let { definition ->
            DictionaryDialog(
                definition = definition,
                onSpeak = { viewModel.speakDefinitionWord() },
                onDismiss = { viewModel.closeDefinition() }
            )
        }

        // Level Won Dialog
        if (uiState.isCompleted) {
            LevelCompleteDialog(
                levelNumber = uiState.levelNumber,
                stars = uiState.earnedStars,
                earnedCoins = uiState.earnedCoins,
                timeTakenSec = uiState.elapsedSec,
                isLastLevel = uiState.levelNumber >= 100,
                onNextLevel = {
                    val nextLvl = (uiState.levelNumber + 1).coerceAtMost(100)
                    viewModel.startLevel(nextLvl)
                },
                onReplay = {
                    viewModel.startLevel(uiState.levelNumber)
                },
                onDismiss = {
                    onBackToHub()
                }
            )
        }
    }
}
