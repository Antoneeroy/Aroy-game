package com.example.ui.hub

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.GamePreferences
import com.example.data.LevelSelectTarget
import com.example.data.PuzzleDifficulty
import com.example.ui.components.CoinBadge
import com.example.ui.components.DailyMysteryDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonGradientButton
import com.example.ui.components.ProfileDashboardDialog
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBgStart
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
import com.example.util.AudioHapticsManager

@Composable
fun HubScreen(
    preferences: GamePreferences,
    audioHaptics: AudioHapticsManager,
    onStartPhotoPuzzle: (level: Int, difficulty: PuzzleDifficulty, customUri: String?) -> Unit,
    onStartWordMaster: (level: Int) -> Unit,
    onStartAnagramBlitz: () -> Unit,
    onOpenLevelSelect: (LevelSelectTarget) -> Unit,
    onStartSnakeLudo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showProfileDialog by remember { mutableStateOf(false) }
    var showDailyDialog by remember { mutableStateOf(false) }
    var soundOn by remember { mutableStateOf(preferences.soundEnabled) }
    var showFreeCoinsBanner by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onStartPhotoPuzzle(1, PuzzleDifficulty.EASY, uri.toString())
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgStart)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(max = 600.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GlowGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Aroy Puzzle",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Aroy Puzzle",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Dual Brain Game Hub",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CoinBadge(
                        coins = preferences.coins,
                        onClick = {
                            preferences.addCoins(50)
                            showFreeCoinsBanner = true
                            audioHaptics.playHintChime()
                        },
                        modifier = Modifier.testTag("hub_coins_badge")
                    )

                    IconButton(
                        onClick = {
                            val next = !soundOn
                            soundOn = next
                            preferences.soundEnabled = next
                            audioHaptics.soundEnabled = next
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GlassSurface)
                            .border(1.dp, GlassBorderSubtle, CircleShape)
                            .testTag("hub_sound_toggle")
                    ) {
                        Icon(
                            imageVector = if (soundOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Sound Toggle",
                            tint = if (soundOn) CyanAccent else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Quick Stats & Daily Mystery Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Profile & Brain IQ Card
                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = { showProfileDialog = true },
                    testTag = "brain_iq_card"
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x3338BDF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🧠", fontSize = 18.sp)
                        }
                        Column {
                            Text("Brain IQ", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("${preferences.brainIqScore} pts", color = CyanAccent, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            Text(preferences.playerRank, color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Daily Mystery & Streak Card
                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = { showDailyDialog = true },
                    testTag = "daily_streak_card"
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x33F59E0B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔥", fontSize = 18.sp)
                        }
                        Column {
                            Text("Daily Streak", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("${preferences.streakCount} Days", color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            Text(if (preferences.canClaimDailyReward) "Reward Ready!" else "Active", color = EmeraldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // MODE 1: HD PHOTO JIGSAW PUZZLE CARD
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                backgroundColor = Color(0xD9101C30),
                borderColor = CyanAccent.copy(alpha = 0.5f),
                testTag = "photo_puzzle_mode_card"
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyanAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Extension, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(20.dp))
                            }
                            Text(
                                text = "MODE 1",
                                color = CyanAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        val unlockedEasy = preferences.getPhotoUnlockedLevel("easy")
                        Text(
                            text = "Level $unlockedEasy/100",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "HD Photo Jigsaw Puzzle",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "300 Levels • Real-time slicing • 16 to 64 pieces • Magnetic snap • Animals, Space, Nature, Flowers & your own photos!",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NeonGradientButton(
                            text = "Play Now",
                            onClick = {
                                val currentLvl = preferences.getPhotoUnlockedLevel("easy")
                                onStartPhotoPuzzle(currentLvl, PuzzleDifficulty.EASY, null)
                            },
                            icon = Icons.Default.PlayArrow,
                            modifier = Modifier.weight(1.3f),
                            testTag = "play_photo_puzzle_button"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(GlassSurface)
                                .border(1.dp, GlassBorder, RoundedCornerShape(25.dp))
                                .clickable { onOpenLevelSelect(LevelSelectTarget.PHOTO_EASY) }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.ListAlt, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                                Text("Levels", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Play Own Photo shortcut
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(GlassSurface)
                                .border(1.dp, VioletAccent.copy(alpha = 0.5f), CircleShape)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .testTag("hub_play_own_photo_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Play Own Photo", tint = VioletAccent, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }

            // MODE 2: ENGLISH WORD MASTER CARD
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                backgroundColor = Color(0xD91B142F),
                borderColor = VioletAccent.copy(alpha = 0.5f),
                testTag = "word_master_mode_card"
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VioletAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Spellcheck, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Text(
                                text = "MODE 2",
                                color = VioletAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "Level ${preferences.wordMasterUnlockedLevel}/100",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "English Word Master",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Word wheel swipe • Picture clue unblur • Instant English Dictionary & TTS audio pronunciation • Bonus word jar!",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NeonGradientButton(
                            text = "Play Now",
                            onClick = { onStartWordMaster(preferences.wordMasterUnlockedLevel) },
                            icon = Icons.Default.PlayArrow,
                            modifier = Modifier.weight(1.3f),
                            testTag = "play_word_master_button"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(GlassSurface)
                                .border(1.dp, GlassBorder, RoundedCornerShape(25.dp))
                                .clickable { onOpenLevelSelect(LevelSelectTarget.WORD_MASTER) }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.ListAlt, contentDescription = null, tint = VioletAccent, modifier = Modifier.size(16.dp))
                                Text("Levels", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // MODE 3: SNAKE LUDO (SAANP SEEDHI) CARD
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                backgroundColor = Color(0xD9261B0E),
                borderColor = GoldAccent.copy(alpha = 0.6f),
                testTag = "snake_ludo_mode_card"
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoldAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Casino, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            }
                            Text(
                                text = "MODE 3",
                                color = GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(GoldAccent.copy(alpha = 0.2f))
                                .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "10 Themes • 4 Levels",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Snake Ludo (Saanp Seedhi)",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "10x10 Classic Brain Board • 4 Difficulties (Easy, Middle, Hard, Very Hard) • 10 Unique Visual Themes • 3D Animated Dice • Solo vs Smart AI",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Difficulty badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("🟢 Easy", "🟡 Middle", "🟠 Hard", "🔴 Very Hard").forEach { label ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GlassSurface)
                                    .border(0.5.dp, GlassBorderSubtle, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    NeonGradientButton(
                        text = "Play Snake Ludo",
                        onClick = onStartSnakeLudo,
                        icon = Icons.Default.PlayArrow,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "play_snake_ludo_button"
                    )
                }
            }

            // MINI GAME: FLASH ANAGRAM BLITZ
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                backgroundColor = Color(0xD91B2418),
                borderColor = EmeraldAccent.copy(alpha = 0.5f),
                onClick = onStartAnagramBlitz,
                testTag = "anagram_blitz_banner"
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0x3310B981)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                        }
                        Column {
                            Text("FLASH ANAGRAM BLITZ", color = EmeraldAccent, fontSize = 13.sp, fontWeight = FontWeight.Black)
                            Text("60-Second Rapid-Fire Anagram Challenge", color = TextSecondary, fontSize = 11.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(GlowGradient)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("PLAY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Rewarded Coins Ad-Ready Slot
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x401E293B))
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .clickable {
                        preferences.addCoins(50)
                        showFreeCoinsBanner = true
                        audioHaptics.playVictoryFanfare()
                    }
                    .padding(14.dp)
                    .testTag("claim_free_coins_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                    Text("Claim +50 Free Bonus Coins", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Profile Dialog
        if (showProfileDialog) {
            ProfileDashboardDialog(
                brainIq = preferences.brainIqScore,
                rank = preferences.playerRank,
                totalPhotoSolved = preferences.totalPhotoSolved,
                totalWordsFound = preferences.totalWordsFound,
                streakCount = preferences.streakCount,
                fastestSec = preferences.fastestPhotoSec,
                coins = preferences.coins,
                onDismiss = { showProfileDialog = false }
            )
        }

        // Daily Mystery Dialog
        if (showDailyDialog) {
            DailyMysteryDialog(
                streakCount = preferences.streakCount,
                canClaim = preferences.canClaimDailyReward,
                onClaimReward = {
                    val earned = preferences.claimDailyReward()
                    if (earned > 0) audioHaptics.playVictoryFanfare()
                },
                onPlayPhotoOfTheDay = {
                    showDailyDialog = false
                    onStartPhotoPuzzle(7, PuzzleDifficulty.MEDIUM, null)
                },
                onPlayWordOfTheDay = {
                    showDailyDialog = false
                    onStartWordMaster(5)
                },
                onDismiss = { showDailyDialog = false }
            )
        }
    }
}
