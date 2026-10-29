package com.example.ui.hub

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LevelSelectScreen(
    targetMode: LevelSelectTarget,
    preferences: GamePreferences,
    onSelectLevel: (Int, PuzzleDifficulty, PuzzleCategory) -> Unit,
    onBackToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember {
        mutableIntStateOf(
            when (targetMode) {
                LevelSelectTarget.PHOTO_EASY -> 0
                LevelSelectTarget.PHOTO_MEDIUM -> 1
                LevelSelectTarget.PHOTO_HARD -> 2
                LevelSelectTarget.PHOTO_EXPERT -> 3
            }
        )
    }

    var selectedCategoryFilter by remember { mutableStateOf<PuzzleCategory?>(null) }

    val tabDifficulties = listOf(
        PuzzleDifficulty.EASY,
        PuzzleDifficulty.MEDIUM,
        PuzzleDifficulty.HARD,
        PuzzleDifficulty.EXPERT
    )

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
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToHub, modifier = Modifier.testTag("level_select_back_button")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }

                Text(
                    text = "Select Puzzle Level",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                CoinBadge(coins = preferences.coins)
            }

            // Difficulty Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0x331E293B),
                contentColor = CyanAccent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyanAccent
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                tabDifficulties.forEachIndexed { index, diff ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = diff.name.lowercase().replaceFirstChar { it.uppercase() } + " (${diff.totalPieces}p)",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) TextPrimary else TextSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Carousel
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    val isAll = (selectedCategoryFilter == null)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isAll) CyanAccent.copy(alpha = 0.25f) else Color(0x331E293B))
                            .border(1.dp, if (isAll) CyanAccent else GlassBorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { selectedCategoryFilter = null }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("✨ All Categories", color = if (isAll) Color.White else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                items(PuzzleCategory.entries) { cat ->
                    val isSelected = (selectedCategoryFilter == cat)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) CyanAccent.copy(alpha = 0.25f) else Color(0x331E293B))
                            .border(1.dp, if (isSelected) CyanAccent else GlassBorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { selectedCategoryFilter = cat }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(cat.iconEmoji, fontSize = 12.sp)
                            Text(cat.displayName, color = if (isSelected) Color.White else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val currentDifficulty = tabDifficulties[selectedTab]
            val unlockedMax = preferences.getPhotoUnlockedLevel(currentDifficulty.name.lowercase())

            val allLevels = (1..100).toList().filter { lvl ->
                if (selectedCategoryFilter == null) true
                else PuzzleCategory.forLevel(lvl) == selectedCategoryFilter
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(allLevels) { lvl ->
                    val isUnlocked = lvl <= unlockedMax
                    val stars = preferences.getPhotoStars(currentDifficulty.name.lowercase(), lvl)
                    val category = PuzzleCategory.forLevel(lvl)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isUnlocked) GlassSurfaceElevated else Color(0x33101626))
                            .border(
                                1.dp,
                                if (isUnlocked) GlassBorder else GlassBorderSubtle,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable(enabled = isUnlocked) {
                                onSelectLevel(lvl, currentDifficulty, category)
                            }
                            .testTag("level_button_$lvl"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "$lvl",
                                    color = CyanAccent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(category.iconEmoji, fontSize = 10.sp)
                                    Text(
                                        text = category.displayName.take(5),
                                        color = TextMuted,
                                        fontSize = 9.sp
                                    )
                                }
                                if (stars > 0) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        for (s in 1..stars) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = GoldAccent,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
