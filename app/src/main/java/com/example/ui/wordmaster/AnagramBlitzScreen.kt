package com.example.ui.wordmaster

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GamePreferences
import com.example.data.WordMasterLevelRepository
import com.example.ui.components.CoinBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassOutlineButton
import com.example.ui.components.NeonGradientButton
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoseAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AudioHapticsManager
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnagramBlitzScreen(
    preferences: GamePreferences,
    audioHaptics: AudioHapticsManager,
    onBackToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val blitzPacks: List<Pair<List<Char>, List<String>>> = remember { WordMasterLevelRepository.anagramBlitzWordSets }
    var currentPackIndex by remember { mutableIntStateOf(0) }
    var letters by remember { mutableStateOf<List<Char>>(blitzPacks[0].first) }
    var validWords by remember { mutableStateOf<Set<String>>(blitzPacks[0].second.toSet()) }

    var timeLeftSec by remember { mutableIntStateOf(60) }
    var isRunning by remember { mutableStateOf(true) }
    var isGameOver by remember { mutableStateOf(false) }
    val discoveredWords = remember { mutableStateListOf<String>() }
    var score by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRunning, timeLeftSec) {
        if (isRunning && timeLeftSec > 0) {
            delay(1000)
            timeLeftSec -= 1
            if (timeLeftSec == 0) {
                isRunning = false
                isGameOver = true
                audioHaptics.playVictoryFanfare()
                audioHaptics.vibrateSuccess()
                val coinsEarned = discoveredWords.size * 10
                preferences.addCoins(coinsEarned)
                preferences.recordWordsFound(discoveredWords.size)
            }
        }
    }

    BackHandler {
        onBackToHub()
    }

    fun restart() {
        val nextIdx = (currentPackIndex + 1) % blitzPacks.size
        currentPackIndex = nextIdx
        letters = blitzPacks[nextIdx].first.shuffled()
        validWords = blitzPacks[nextIdx].second.toSet()
        discoveredWords.clear()
        score = 0
        timeLeftSec = 60
        isRunning = true
        isGameOver = false
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToHub, modifier = Modifier.testTag("blitz_back_button")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                    Text("FLASH ANAGRAM BLITZ", color = GoldAccent, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }

                CoinBadge(coins = preferences.coins)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x3310B981))
                    .border(1.dp, if (timeLeftSec <= 15) RoseAccent else EmeraldAccent, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TIME LEFT", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${timeLeftSec}s",
                        color = if (timeLeftSec <= 15) RoseAccent else TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("WORDS FOUND", color = TextMuted, fontSize = 11.sp)
                    Text("${discoveredWords.size}", color = CyanAccent, fontSize = 24.sp, fontWeight = FontWeight.Black)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("BLITZ SCORE", color = TextMuted, fontSize = 11.sp)
                    Text("$score", color = GoldAccent, fontSize = 24.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassSurface)
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(16.dp))
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                if (discoveredWords.isEmpty()) {
                    Text("Swipe the wheel to discover words before time runs out!", color = TextSecondary, fontSize = 13.sp)
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        discoveredWords.forEach { word ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x4010B981))
                                    .border(1.dp, EmeraldAccent.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(word, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            WordWheel(
                letters = letters,
                onWordCompleted = { word ->
                    val upper = word.uppercase()
                    if (validWords.contains(upper)) {
                        if (!discoveredWords.contains(upper)) {
                            discoveredWords.add(upper)
                            score += upper.length * 10
                            audioHaptics.playWordConnectTone()
                            audioHaptics.vibrateSuccess()
                        } else {
                            audioHaptics.playErrorBuzz()
                        }
                    } else {
                        audioHaptics.playErrorBuzz()
                        audioHaptics.vibrateError()
                    }
                },
                onLetterSelected = { audioHaptics.vibrateWordLetter() },
                onShuffle = {
                    letters = letters.shuffled()
                    audioHaptics.vibrateWordLetter()
                },
                wheelSize = 220.dp
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        if (isGameOver) {
            Dialog(onDismissRequest = { isGameOver = false; onBackToHub() }) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    backgroundColor = Color(0xF70E172A),
                    borderColor = GoldAccent.copy(alpha = 0.6f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⚡ TIME'S UP! ⚡", color = GoldAccent, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Blitz Run Complete", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Total Words: ${discoveredWords.size}", color = CyanAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Final Score: $score Points", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("+${discoveredWords.size * 10} Coins Earned!", color = GoldAccent, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GlassOutlineButton(
                                text = "Exit",
                                onClick = onBackToHub,
                                modifier = Modifier.weight(1f)
                            )
                            NeonGradientButton(
                                text = "Play Again",
                                onClick = { restart() },
                                icon = Icons.Default.Replay,
                                modifier = Modifier.weight(1.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}
