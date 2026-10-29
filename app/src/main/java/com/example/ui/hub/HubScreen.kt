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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
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
import com.example.data.PuzzleCategory
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
import com.example.ui.theme.GlowGradient
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldGradient
import com.example.ui.theme.RoseAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import com.example.util.AudioHapticsManager

@Composable
fun HubScreen(
    preferences: GamePreferences,
    audioHaptics: AudioHapticsManager,
    onStartPhotoPuzzle: (level: Int, difficulty: PuzzleDifficulty, customUri: String?, category: PuzzleCategory?) -> Unit,
    onOpenLevelSelect: (LevelSelectTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    var showProfileDialog by remember { mutableStateOf(false) }
    var showDailyDialog by remember { mutableStateOf(false) }
    var soundOn by remember { mutableStateOf(preferences.soundEnabled) }
    var selectedCategory by remember { mutableStateOf(PuzzleCategory.ANIMALS) }
    var selectedDifficulty by remember { mutableStateOf(PuzzleDifficulty.EASY) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onStartPhotoPuzzle(1, selectedDifficulty, uri.toString(), selectedCategory)
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
            // 1. Top App Bar
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(GlowGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Extension,
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
                            text = "HD Jigsaw Puzzle Studio",
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

            // 2. Stats & Daily Streak Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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
                            Text("Daily Challenge", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("${preferences.streakCount} Days", color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Black)
                            Text(if (preferences.canClaimDailyReward) "Reward Ready!" else "Play Daily", color = EmeraldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Category Selector Section ("All image aye: Animals, Flower, etc.")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SELECT PHOTO CATEGORY",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${PuzzleCategory.entries.size} Categories",
                    color = CyanAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PuzzleCategory.entries) { cat ->
                    val isSelected = (selectedCategory == cat)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) CyanAccent.copy(alpha = 0.25f) else Color(0x331E293B))
                            .border(if (isSelected) 1.5.dp else 0.8.dp, if (isSelected) CyanAccent else GlassBorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("category_chip_${cat.name.lowercase()}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(cat.iconEmoji, fontSize = 16.sp)
                            Column {
                                Text(
                                    text = cat.displayName,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // 4. Active Category Featured Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                backgroundColor = Color(0xD9101C30),
                borderColor = CyanAccent.copy(alpha = 0.6f),
                testTag = "featured_category_card"
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(selectedCategory.iconEmoji, fontSize = 28.sp)
                            Column {
                                Text(
                                    text = selectedCategory.displayName,
                                    color = TextPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = selectedCategory.subtitle,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyanAccent.copy(alpha = 0.2f))
                                .border(1.dp, CyanAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("100 HD Levels", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Difficulty Selector within card
                    Text("CHOOSE PIECES & DIFFICULTY", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            PuzzleDifficulty.EASY to "16 Pcs (4x4)",
                            PuzzleDifficulty.MEDIUM to "36 Pcs (6x6)",
                            PuzzleDifficulty.HARD to "48 Pcs (8x6)",
                            PuzzleDifficulty.EXPERT to "64 Pcs (8x8)"
                        ).forEach { (diff, label) ->
                            val isDiffSelected = (selectedDifficulty == diff)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDiffSelected) CyanAccent.copy(alpha = 0.25f) else Color(0x330F172A))
                                    .border(1.dp, if (isDiffSelected) CyanAccent else GlassBorderSubtle, RoundedCornerShape(10.dp))
                                    .clickable { selectedDifficulty = diff }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = diff.name.lowercase().replaceFirstChar { it.uppercase() },
                                        color = if (isDiffSelected) Color.White else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${diff.totalPieces}p",
                                        color = if (isDiffSelected) CyanAccent else TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NeonGradientButton(
                            text = "Play ${selectedCategory.displayName}",
                            onClick = {
                                val currentLvl = preferences.getPhotoUnlockedLevel(selectedDifficulty.name.lowercase())
                                onStartPhotoPuzzle(currentLvl, selectedDifficulty, null, selectedCategory)
                            },
                            icon = Icons.Default.PlayArrow,
                            modifier = Modifier.weight(1.3f),
                            testTag = "play_category_button"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(GlassSurface)
                                .border(1.dp, GlassBorder, RoundedCornerShape(25.dp))
                                .clickable {
                                    val target = when (selectedDifficulty) {
                                        PuzzleDifficulty.EASY -> LevelSelectTarget.PHOTO_EASY
                                        PuzzleDifficulty.MEDIUM -> LevelSelectTarget.PHOTO_MEDIUM
                                        PuzzleDifficulty.HARD -> LevelSelectTarget.PHOTO_HARD
                                        PuzzleDifficulty.EXPERT -> LevelSelectTarget.PHOTO_EXPERT
                                    }
                                    onOpenLevelSelect(target)
                                }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.ListAlt, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                                Text("All Levels", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // 5. "Play Your Own Photo" Custom Gallery Upload Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                backgroundColor = Color(0xD91E1333),
                borderColor = VioletAccent.copy(alpha = 0.5f),
                testTag = "play_own_photo_card"
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
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(VioletAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Column {
                            Text("Play Your Own Photo", color = VioletAccent, fontSize = 15.sp, fontWeight = FontWeight.Black)
                            Text("Upload from camera or phone gallery", color = TextSecondary, fontSize = 11.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(VioletAccent)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("upload_custom_photo_button")
                    ) {
                        Text("SELECT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // 6. Free Bonus Coins Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x401E293B))
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .clickable {
                        preferences.addCoins(50)
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
                totalWordsFound = 0,
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
                    onStartPhotoPuzzle(7, PuzzleDifficulty.MEDIUM, null, PuzzleCategory.FLOWERS)
                },
                onDismiss = { showDailyDialog = false }
            )
        }
    }
}
