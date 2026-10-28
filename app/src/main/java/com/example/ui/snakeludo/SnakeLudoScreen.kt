package com.example.ui.snakeludo

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GamePreferences
import com.example.ui.components.CoinBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassOutlineButton
import com.example.ui.components.NeonGradientButton
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GlowGradient
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoseAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun SnakeLudoScreen(
    viewModel: SnakeLudoViewModel,
    preferences: GamePreferences,
    onBackToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMenuDialog by remember { mutableStateOf(false) }
    var showThemesDrawer by remember { mutableStateOf(false) }

    BackHandler {
        onBackToHub()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(uiState.theme.boardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .widthIn(max = 600.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header Bar with 3-Line Menu Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onBackToHub, modifier = Modifier.testTag("snake_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }

                    // 3-Line Menu Button (User requested: "3 line bhi add karo")
                    IconButton(
                        onClick = { showMenuDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GlassSurface)
                            .border(1.dp, uiState.theme.primaryColor.copy(alpha = 0.5f), CircleShape)
                            .testTag("snake_3_line_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "3-Line Options Menu",
                            tint = uiState.theme.primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(uiState.theme.accentIcon, fontSize = 16.sp)
                        Text(
                            text = "Snake Ludo",
                            color = uiState.theme.primaryColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "${uiState.playerMode.displayName} • ${uiState.difficulty.displayName}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick Art / Vector Toggle (when boardDrawableRes is present)
                    if (uiState.theme.boardDrawableRes != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (uiState.useArtBoard) uiState.theme.primaryColor.copy(alpha = 0.25f) else GlassSurface)
                                .border(1.dp, if (uiState.useArtBoard) uiState.theme.primaryColor else GlassBorderSubtle, RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleArtBoard() }
                                .padding(horizontal = 7.dp, vertical = 5.dp)
                                .testTag("art_board_toggle_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(if (uiState.useArtBoard) "🖼️ Board Art" else "📐 Vector Grid", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Quick Theme Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassSurface)
                            .border(1.dp, uiState.theme.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { showThemesDrawer = !showThemesDrawer }
                            .padding(horizontal = 7.dp, vertical = 5.dp)
                            .testTag("theme_picker_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Default.Palette, contentDescription = "Themes", tint = uiState.theme.primaryColor, modifier = Modifier.size(15.dp))
                            Text("Theme", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    CoinBadge(coins = preferences.coins)
                }
            }

            // 10-Theme Picker (Collapsible)
            AnimatedVisibility(visible = showThemesDrawer) {
                Column(modifier = Modifier.padding(bottom = 6.dp)) {
                    Text("CHOOSE BOARD THEME (10 THEMES)", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(SnakeTheme.entries) { theme ->
                            val isSelected = uiState.theme == theme
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) theme.primaryColor.copy(alpha = 0.25f) else Color(0x331E293B))
                                    .border(if (isSelected) 1.5.dp else 0.8.dp, if (isSelected) theme.primaryColor else GlassBorderSubtle, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setTheme(theme) }
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(theme.accentIcon, fontSize = 13.sp)
                                    Text(theme.title, color = if (isSelected) Color.White else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 2. Active Players Status Bar (2, 3, 4 Office Players + Standings)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x440F172A))
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                uiState.players.forEachIndexed { idx, player ->
                    val isCurrent = (uiState.currentPlayerIndex == idx)
                    val isFinished = (player.rank != null)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCurrent) player.color.copy(alpha = 0.25f) else Color.Transparent)
                            .border(if (isCurrent) 1.5.dp else 0.5.dp, if (isCurrent) player.color else Color.Transparent, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(player.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${player.id + 1}", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                            Column {
                                Text(
                                    text = player.name,
                                    color = if (isCurrent) Color.White else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = if (isFinished) "Finished #${player.rank} 🏆" else "Tile ${player.pos}",
                                    color = if (isCurrent) player.color else TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Realistic Clean & Professional 10x10 Snake & Ladder Board
            val isArtMode = uiState.useArtBoard && uiState.theme.boardDrawableRes != null

            // 3. Realistic Clean & Professional 10x10 Snake & Ladder Board
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(uiState.theme.boardBg)
                    .border(2.5.dp, uiState.theme.primaryColor.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    .shadow(14.dp, RoundedCornerShape(16.dp), spotColor = uiState.theme.primaryColor)
            ) {
                val boardRes = uiState.theme.boardDrawableRes
                if (isArtMode && boardRes != null) {
                    Image(
                        painter = painterResource(boardRes),
                        contentDescription = "Classic Snakes and Ladders Board",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val cellW = w / 10f
                    val cellH = h / 10f

                    // Helper to get center offset of any tile 1..100 in zigzag sequence
                    fun tileCenter(num: Int): Offset {
                        val n = (num - 1).coerceIn(0, 99)
                        val r = 9 - (n / 10) // row from top (0 at top, 9 at bottom)
                        val rowIdx = n / 10
                        val c = if (rowIdx % 2 == 0) (n % 10) else (9 - (n % 10)) // zigzag direction
                        return Offset((c + 0.5f) * cellW, (r + 0.5f) * cellH)
                    }

                    if (!isArtMode) {
                        // A. Draw Vibrant Beveled Physical Tiles across all 10 themes
                        val palette = uiState.theme.tilePalette
                        for (row in 0 until 10) {
                            for (col in 0 until 10) {
                                val rowIdx = 9 - row
                                val tileNum = if (rowIdx % 2 == 0) rowIdx * 10 + col + 1 else rowIdx * 10 + (9 - col) + 1
                                val baseCellColor = palette[(tileNum - 1) % palette.size]

                                // Fill tile
                                drawRect(
                                    color = baseCellColor,
                                    topLeft = Offset(col * cellW, row * cellH),
                                    size = androidx.compose.ui.geometry.Size(cellW, cellH)
                                )
                                // 3D Top-Left Inner Gloss Highlight
                                drawRect(
                                    color = Color(0x28FFFFFF),
                                    topLeft = Offset(col * cellW + 1f, row * cellH + 1f),
                                    size = androidx.compose.ui.geometry.Size(cellW - 2f, cellH - 2f),
                                    style = Stroke(width = 1f)
                                )
                                // Clean Crisp Outer Bevel Border
                                drawRect(
                                    color = Color(0x44000000),
                                    topLeft = Offset(col * cellW, row * cellH),
                                    size = androidx.compose.ui.geometry.Size(cellW, cellH),
                                    style = Stroke(width = 1.4f)
                                )
                            }
                        }

                        // Special Start (Tile 1) and Finish (Tile 100) glow highlight
                        val p1Center = tileCenter(1)
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x6610B981), Color.Transparent),
                                center = p1Center,
                                radius = cellW * 0.8f
                            ),
                            topLeft = Offset(p1Center.x - cellW * 0.5f, p1Center.y - cellH * 0.5f),
                            size = androidx.compose.ui.geometry.Size(cellW, cellH)
                        )

                        val p100Center = tileCenter(100)
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x88F59E0B), Color.Transparent),
                                center = p100Center,
                                radius = cellW * 0.85f
                            ),
                            topLeft = Offset(p100Center.x - cellW * 0.5f, p100Center.y - cellH * 0.5f),
                            size = androidx.compose.ui.geometry.Size(cellW, cellH)
                        )

                        // B. Draw Realistic 3D Wooden Ladders with Mahogany Beams, Rung Highlights & Brass Rivets
                        for (ladder in uiState.ladders) {
                            val pFoot = tileCenter(ladder.foot)
                            val pTop = tileCenter(ladder.top)

                            val dx = pTop.x - pFoot.x
                            val dy = pTop.y - pFoot.y
                            val len = hypot(dx.toDouble(), dy.toDouble()).toFloat()
                            if (len > 0) {
                                val railSpacing = 10f
                                val nx = -dy / len * railSpacing
                                val ny = dx / len * railSpacing

                                // Drop shadow for both side rails
                                val shadowOffset = 4.5f
                                drawLine(
                                    color = Color(0x66000000),
                                    start = Offset(pFoot.x - nx + shadowOffset, pFoot.y - ny + shadowOffset),
                                    end = Offset(pTop.x - nx + shadowOffset, pTop.y - ny + shadowOffset),
                                    strokeWidth = 6.5f,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    color = Color(0x66000000),
                                    start = Offset(pFoot.x + nx + shadowOffset, pFoot.y + ny + shadowOffset),
                                    end = Offset(pTop.x + nx + shadowOffset, pTop.y + ny + shadowOffset),
                                    strokeWidth = 6.5f,
                                    cap = StrokeCap.Round
                                )

                                // Left & right wooden rails (Dark Mahogany with 3D light edge highlight)
                                val woodBase = Color(0xFF78350F)
                                val woodHighlight = Color(0xFFD97706)

                                drawLine(
                                    color = woodBase,
                                    start = Offset(pFoot.x - nx, pFoot.y - ny),
                                    end = Offset(pTop.x - nx, pTop.y - ny),
                                    strokeWidth = 5.8f,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    color = woodHighlight,
                                    start = Offset(pFoot.x - nx - 0.8f, pFoot.y - ny - 0.8f),
                                    end = Offset(pTop.x - nx - 0.8f, pTop.y - ny - 0.8f),
                                    strokeWidth = 1.8f,
                                    cap = StrokeCap.Round
                                )

                                drawLine(
                                    color = woodBase,
                                    start = Offset(pFoot.x + nx, pFoot.y + ny),
                                    end = Offset(pTop.x + nx, pTop.y + ny),
                                    strokeWidth = 5.8f,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    color = woodHighlight,
                                    start = Offset(pFoot.x + nx - 0.8f, pFoot.y + ny - 0.8f),
                                    end = Offset(pTop.x + nx - 0.8f, pTop.y + ny - 0.8f),
                                    strokeWidth = 1.8f,
                                    cap = StrokeCap.Round
                                )

                                // Cylindrical Rungs with 3D highlight and brass joint rivets
                                val rungs = (len / 18f).toInt().coerceAtLeast(3)
                                for (k in 1 until rungs) {
                                    val t = k.toFloat() / rungs
                                    val cx = pFoot.x + t * dx
                                    val cy = pFoot.y + t * dy

                                    // Rung shadow
                                    drawLine(
                                        color = Color(0x66000000),
                                        start = Offset(cx - nx + shadowOffset * 0.7f, cy - ny + shadowOffset * 0.7f),
                                        end = Offset(cx + nx + shadowOffset * 0.7f, cy + ny + shadowOffset * 0.7f),
                                        strokeWidth = 4f,
                                        cap = StrokeCap.Round
                                    )
                                    // Rung wood cylinder
                                    drawLine(
                                        color = Color(0xFF92400E),
                                        start = Offset(cx - nx, cy - ny),
                                        end = Offset(cx + nx, cy + ny),
                                        strokeWidth = 4f,
                                        cap = StrokeCap.Round
                                    )
                                    // Rung top light edge
                                    drawLine(
                                        color = Color(0xFFFDE68A),
                                        start = Offset(cx - nx, cy - ny - 0.8f),
                                        end = Offset(cx + nx, cy + ny - 0.8f),
                                        strokeWidth = 1.8f,
                                        cap = StrokeCap.Round
                                    )
                                    // Left & right brass connection rivets
                                    drawCircle(color = Color(0xFFFBBF24), radius = 2f, center = Offset(cx - nx, cy - ny))
                                    drawCircle(color = Color(0xFF451A03), radius = 0.8f, center = Offset(cx - nx, cy - ny))
                                    drawCircle(color = Color(0xFFFBBF24), radius = 2f, center = Offset(cx + nx, cy + ny))
                                    drawCircle(color = Color(0xFF451A03), radius = 0.8f, center = Offset(cx + nx, cy + ny))
                                }
                            }
                        }

                        // C. Draw Realistic Illustrated Serpents with Tapered Bodies, Scales, Viper Heads & Forked Tongues
                        for ((snakeIdx, snake) in uiState.snakes.withIndex()) {
                            val pHead = tileCenter(snake.head)
                            val pTail = tileCenter(snake.tail)

                            val dx = pTail.x - pHead.x
                            val dy = pTail.y - pHead.y
                            val len = hypot(dx.toDouble(), dy.toDouble()).toFloat()
                            if (len > 0) {
                                // Natural serpentine curvature: S-curve control points
                                val waveAmp = (len * 0.28f).coerceIn(14f, 36f)
                                val nx = -dy / len * waveAmp
                                val ny = dx / len * waveAmp

                                val p1x = pHead.x + dx * 0.30f + nx
                                val p1y = pHead.y + dy * 0.30f + ny
                                val p2x = pHead.x + dx * 0.65f - nx * 0.85f
                                val p2y = pHead.y + dy * 0.65f - ny * 0.85f

                                // Sample points along cubic bezier
                                val steps = 30
                                val points = ArrayList<Offset>(steps + 1)
                                for (step in 0..steps) {
                                    val t = step.toFloat() / steps
                                    val omt = 1f - t
                                    val px = omt * omt * omt * pHead.x +
                                            3f * omt * omt * t * p1x +
                                            3f * omt * t * t * p2x +
                                            t * t * t * pTail.x
                                    val py = omt * omt * omt * pHead.y +
                                            3f * omt * omt * t * p1y +
                                            3f * omt * t * t * p2y +
                                            t * t * t * pTail.y
                                    points.add(Offset(px, py))
                                }

                                // Distinct serpent species based on snake index (Emerald Viper, Coral Snake, Copper Python)
                                val (skinColor, bellyColor, bandColor) = when (snakeIdx % 3) {
                                    0 -> Triple(Color(0xFF15803D), Color(0xFFFEF08A), Color(0xFF052E16)) // Green Emerald Viper
                                    1 -> Triple(Color(0xFFDC2626), Color(0xFFFFFBEB), Color(0xFF0F172A)) // Scarlet Coral Snake
                                    else -> Triple(Color(0xFFB45309), Color(0xFFFEF3C7), Color(0xFF451A03)) // Copper Python
                                }

                                // 1. Deep 3D drop shadow (lifts snake off tiles)
                                val shadowShift = Offset(4f, 5f)
                                for (i in 0 until steps) {
                                    val t = i.toFloat() / steps
                                    val wBase = if (t < 0.15f) 10f + t * 20f else if (t < 0.65f) 14f else (14f * (1f - (t - 0.65f) / 0.35f)).coerceAtLeast(3f)
                                    drawLine(
                                        color = Color(0x66000000),
                                        start = points[i] + shadowShift,
                                        end = points[i + 1] + shadowShift,
                                        strokeWidth = wBase + 5f,
                                        cap = StrokeCap.Round
                                    )
                                }

                                // 2. Main serpent body with realistic anatomical tapering
                                for (i in 0 until steps) {
                                    val t = i.toFloat() / steps
                                    val bodyWidth = if (t < 0.15f) 9f + t * 24f else if (t < 0.60f) 13.5f else (13.5f * (1f - (t - 0.60f) / 0.40f)).coerceAtLeast(2.8f)
                                    drawLine(
                                        color = skinColor,
                                        start = points[i],
                                        end = points[i + 1],
                                        strokeWidth = bodyWidth,
                                        cap = StrokeCap.Round
                                    )
                                }

                                // 3. Underbelly scale stripe
                                for (i in 0 until steps) {
                                    val t = i.toFloat() / steps
                                    if (t in 0.1f..0.85f) {
                                        val bellyWidth = if (t < 0.6f) 4.5f else 2.5f
                                        drawLine(
                                            color = bellyColor,
                                            start = points[i],
                                            end = points[i + 1],
                                            strokeWidth = bellyWidth,
                                            cap = StrokeCap.Round
                                        )
                                    }
                                }

                                // 4. Dorsal scale cross-bands and golden ring spots
                                for (i in 3 until steps - 3 step 2) {
                                    val pA = points[i]
                                    val pB = points[i + 1]
                                    val segDx = pB.x - pA.x
                                    val segDy = pB.y - pA.y
                                    val segLen = hypot(segDx.toDouble(), segDy.toDouble()).toFloat()
                                    if (segLen > 0) {
                                        val segNx = -segDy / segLen * 5.5f
                                        val segNy = segDx / segLen * 5.5f
                                        drawLine(
                                            color = bandColor,
                                            start = Offset(pA.x - segNx, pA.y - segNy),
                                            end = Offset(pA.x + segNx, pA.y + segNy),
                                            strokeWidth = 2.4f,
                                            cap = StrokeCap.Round
                                        )
                                        drawCircle(
                                            color = Color(0xFFFDE047),
                                            radius = 1.2f,
                                            center = Offset(pA.x, pA.y)
                                        )
                                    }
                                }

                                // 5. Realistic Illustrated Viper Head
                                val headAngle = atan2((points[0].y - points[1].y).toDouble(), (points[0].x - points[1].x).toDouble()).toFloat()
                                val headLen = 14f
                                val headWidth = 10f

                                val cosA = cos(headAngle)
                                val sinA = sin(headAngle)
                                val perpX = -sinA * headWidth
                                val perpY = cosA * headWidth

                                val noseX = pHead.x + cosA * headLen
                                val noseY = pHead.y + sinA * headLen

                                // Head shadow
                                val headShadowPath = Path().apply {
                                    moveTo(noseX + shadowShift.x, noseY + shadowShift.y)
                                    lineTo(pHead.x + perpX + shadowShift.x, pHead.y + perpY + shadowShift.y)
                                    lineTo(pHead.x - cosA * 4f + shadowShift.x, pHead.y - sinA * 4f + shadowShift.y)
                                    lineTo(pHead.x - perpX + shadowShift.x, pHead.y - perpY + shadowShift.y)
                                    close()
                                }
                                drawPath(path = headShadowPath, color = Color(0x66000000))

                                // Main viper skull
                                val headPath = Path().apply {
                                    moveTo(noseX, noseY)
                                    lineTo(pHead.x + perpX, pHead.y + perpY)
                                    lineTo(pHead.x - cosA * 4f, pHead.y - sinA * 4f)
                                    lineTo(pHead.x - perpX, pHead.y - perpY)
                                    close()
                                }
                                drawPath(path = headPath, color = skinColor)
                                drawPath(path = headPath, color = Color(0x88000000), style = Stroke(width = 1.2f))

                                // Crown scale plate
                                val crownPath = Path().apply {
                                    moveTo(noseX - cosA * 3f, noseY - sinA * 3f)
                                    lineTo(pHead.x + perpX * 0.5f, pHead.y + perpY * 0.5f)
                                    lineTo(pHead.x - perpX * 0.5f, pHead.y - perpY * 0.5f)
                                    close()
                                }
                                drawPath(path = crownPath, color = bandColor)

                                // Predator glowing eyes with slit pupils
                                val eyeOffsetX = cosA * (headLen * 0.45f)
                                val eyeOffsetY = sinA * (headLen * 0.45f)
                                val eyePerpX = -sinA * (headWidth * 0.65f)
                                val eyePerpY = cosA * (headWidth * 0.65f)

                                val eyeLeft = Offset(pHead.x + eyeOffsetX + eyePerpX, pHead.y + eyeOffsetY + eyePerpY)
                                val eyeRight = Offset(pHead.x + eyeOffsetX - eyePerpX, pHead.y + eyeOffsetY - eyePerpY)

                                drawCircle(color = Color(0xFFFDE047), radius = 2.4f, center = eyeLeft)
                                drawCircle(color = Color.Black, radius = 1.3f, center = eyeLeft)
                                drawCircle(color = Color(0xFFFDE047), radius = 2.4f, center = eyeRight)
                                drawCircle(color = Color.Black, radius = 1.3f, center = eyeRight)

                                // Nostril pits
                                val nostrilX = noseX - cosA * 2.5f
                                val nostrilY = noseY - sinA * 2.5f
                                drawCircle(color = Color.Black, radius = 0.8f, center = Offset(nostrilX - sinA * 2f, nostrilY + cosA * 2f))
                                drawCircle(color = Color.Black, radius = 0.8f, center = Offset(nostrilX + sinA * 2f, nostrilY - cosA * 2f))

                                // Flicking crimson forked tongue
                                val tongueBaseX = noseX
                                val tongueBaseY = noseY
                                val tongueStemLen = 8f
                                val tongueTipX = tongueBaseX + cosA * tongueStemLen
                                val tongueTipY = tongueBaseY + sinA * tongueStemLen
                                val forkLen = 4.5f
                                val forkSpread = 0.45f
                                val fork1X = tongueTipX + cos(headAngle + forkSpread) * forkLen
                                val fork1Y = tongueTipY + sin(headAngle + forkSpread) * forkLen
                                val fork2X = tongueTipX + cos(headAngle - forkSpread) * forkLen
                                val fork2Y = tongueTipY + sin(headAngle - forkSpread) * forkLen

                                drawLine(color = Color(0xFFDC2626), start = Offset(tongueBaseX, tongueBaseY), end = Offset(tongueTipX, tongueTipY), strokeWidth = 1.8f)
                                drawLine(color = Color(0xFFDC2626), start = Offset(tongueTipX, tongueTipY), end = Offset(fork1X, fork1Y), strokeWidth = 1.5f)
                                drawLine(color = Color(0xFFDC2626), start = Offset(tongueTipX, tongueTipY), end = Offset(fork2X, fork2Y), strokeWidth = 1.5f)

                                // 6. Tail tip with golden rattle rings
                                drawCircle(color = Color(0xFFFBBF24), radius = 2.8f, center = pTail)
                                drawCircle(color = Color(0xFF78350F), radius = 1.5f, center = pTail)
                            }
                        }
                    } else {
                        // Art Mode: board art has snakes & ladders, draw subtle target / status highlights
                        val activePlayer = uiState.activePlayer
                        if (activePlayer != null) {
                            val activeCenter = tileCenter(activePlayer.pos)
                            drawCircle(
                                color = activePlayer.color.copy(alpha = 0.35f),
                                radius = cellW * 0.48f,
                                center = activeCenter
                            )
                        }
                    }

                    // D. Draw Multi-Player 3D Ludo Pawns with Specular Shine & Elevation
                    val playersByTile = uiState.players.groupBy { it.pos }

                    for ((tile, playersOnTile) in playersByTile) {
                        val baseCenter = tileCenter(tile)
                        val count = playersOnTile.size

                        playersOnTile.forEachIndexed { i, player ->
                            val offset = when (count) {
                                1 -> Offset.Zero
                                2 -> if (i == 0) Offset(-cellW * 0.22f, 0f) else Offset(cellW * 0.22f, 0f)
                                3 -> when (i) {
                                    0 -> Offset(0f, -cellH * 0.22f)
                                    1 -> Offset(-cellW * 0.22f, cellH * 0.2f)
                                    else -> Offset(cellW * 0.22f, cellH * 0.2f)
                                }
                                else -> when (i) {
                                    0 -> Offset(-cellW * 0.22f, -cellH * 0.22f)
                                    1 -> Offset(cellW * 0.22f, -cellH * 0.22f)
                                    2 -> Offset(-cellW * 0.22f, cellH * 0.22f)
                                    else -> Offset(cellW * 0.22f, cellH * 0.22f)
                                }
                            }

                            val pCenter = Offset(baseCenter.x + offset.x, baseCenter.y + offset.y)
                            val tokenRadius = if (count == 1) cellW * 0.38f else cellW * 0.28f
                            val isTurn = (uiState.activePlayer?.id == player.id)

                            // Active player glow ring
                            if (isTurn) {
                                drawCircle(
                                    color = player.color.copy(alpha = 0.45f),
                                    radius = tokenRadius * 1.5f,
                                    center = pCenter
                                )
                            }

                            // Ground shadow under pawn
                            drawCircle(
                                color = Color(0x77000000),
                                radius = tokenRadius * 1.05f,
                                center = Offset(pCenter.x + 2f, pCenter.y + 3f)
                            )

                            // Base disc of 3D pawn
                            drawCircle(
                                color = player.color,
                                radius = tokenRadius,
                                center = pCenter
                            )

                            // Dark bevel rim
                            drawCircle(
                                color = Color.Black.copy(alpha = 0.45f),
                                radius = tokenRadius,
                                center = pCenter,
                                style = Stroke(width = 1.8f)
                            )

                            // 3D Inner Dome highlight
                            drawCircle(
                                color = Color.White.copy(alpha = 0.45f),
                                radius = tokenRadius * 0.65f,
                                center = Offset(pCenter.x - tokenRadius * 0.18f, pCenter.y - tokenRadius * 0.18f)
                            )

                            // Specular spherical gleam
                            drawCircle(
                                color = Color.White.copy(alpha = 0.95f),
                                radius = tokenRadius * 0.25f,
                                center = Offset(pCenter.x - tokenRadius * 0.28f, pCenter.y - tokenRadius * 0.28f)
                            )
                        }
                    }
                }

                // Tile Numbers Overlay (Clean, Bold, High-Contrast across ALL themes, ZERO childish emojis)
                if (!isArtMode) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        for (row in 0 until 10) {
                            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                for (col in 0 until 10) {
                                    val rowIdx = 9 - row
                                    val tileNum = if (rowIdx % 2 == 0) {
                                        rowIdx * 10 + col + 1
                                    } else {
                                        rowIdx * 10 + (9 - col) + 1
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                            .padding(1.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when (tileNum) {
                                                1 -> "1\nSTART"
                                                100 -> "100\n🏆"
                                                else -> "$tileNum"
                                            },
                                            color = when (tileNum) {
                                                1 -> Color(0xFFFEF08A)
                                                100 -> Color(0xFFFDE047)
                                                else -> Color(0xFFFEF3C7)
                                            },
                                            fontSize = if (tileNum == 1 || tileNum == 100) 8.5.sp else 11.sp,
                                            fontWeight = FontWeight.Black,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            lineHeight = 9.sp,
                                            style = androidx.compose.ui.text.TextStyle(
                                                shadow = androidx.compose.ui.graphics.Shadow(
                                                    color = Color.Black,
                                                    offset = Offset(1.5f, 1.5f),
                                                    blurRadius = 3.5f
                                                )
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Classic Reference Pawn Tray Dock (P1 Red, P2 Blue, Dice, P3 Yellow, P4 Green)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, GlassBorderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                uiState.players.forEachIndexed { idx, player ->
                    val isCurrent = (uiState.currentPlayerIndex == idx)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCurrent) player.color.copy(alpha = 0.2f) else Color.Transparent)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(player.avatarIcon, fontSize = 20.sp)
                        Text(
                            text = if (player.rank != null) "#${player.rank} 🏆" else "Tile ${player.pos}",
                            color = if (isCurrent) player.color else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Current Player Turn & Game Message Banner
            val active = uiState.activePlayer
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = active?.color?.copy(alpha = 0.12f) ?: Color(0x331E293B),
                borderColor = active?.color?.copy(alpha = 0.6f) ?: GlassBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(active?.color ?: CyanAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(active?.avatarIcon ?: "🎲", fontSize = 16.sp)
                        }

                        Column {
                            Text(
                                text = if (active?.isAi == true) "🤖 Robot AI's Turn" else "👉 ${active?.name ?: "Player"}'s Turn",
                                color = active?.color ?: TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = uiState.gameMessage,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }

                    if (active?.rank != null) {
                        Text("Completed! #${active.rank}", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. 3D Animated Dice & Roll Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive 3D Dice Box
                val diceScale by animateFloatAsState(
                    targetValue = if (uiState.isRolling) 1.15f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.6f),
                    label = "diceScale"
                )

                Box(
                    modifier = Modifier
                        .scale(diceScale)
                        .size(68.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFFFFF), Color(0xFFE2E8F0))
                            )
                        )
                        .border(2.dp, active?.color ?: GoldAccent, RoundedCornerShape(16.dp))
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = active?.color ?: GoldAccent)
                        .clickable(enabled = !uiState.isRolling && !uiState.isGameOver && active?.isAi == false) {
                            viewModel.rollDice()
                        }
                        .testTag("snake_dice_box"),
                    contentAlignment = Alignment.Center
                ) {
                    DiceDotsView(dots = uiState.diceValue)
                }

                // Roll Button styled with Active Player's Theme
                NeonGradientButton(
                    text = if (uiState.isRolling) "Rolling..." else if (active?.isAi == true) "AI Rolling..." else "Roll Dice (${active?.name ?: "P1"})",
                    onClick = { viewModel.rollDice() },
                    icon = Icons.Default.Casino,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isRolling && !uiState.isGameOver && active?.isAi == false,
                    testTag = "roll_dice_button"
                )

                // Quick Restart Button
                IconButton(
                    onClick = { viewModel.restartGame() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(GlassSurface)
                        .border(1.dp, GlassBorder, CircleShape)
                        .testTag("snake_restart_button")
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = "Restart", tint = TextSecondary)
                }
            }
        }

        // 3-Line Options Menu Dialog (Mode: Solo, 2P, 3P, 4P office players + Difficulty)
        if (showMenuDialog) {
            Dialog(onDismissRequest = { showMenuDialog = false }) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    backgroundColor = Color(0xF00D1527),
                    borderColor = uiState.theme.primaryColor
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Menu, contentDescription = null, tint = uiState.theme.primaryColor)
                                Text("LUDO OFFICE MENU", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Black)
                            }
                            IconButton(onClick = { showMenuDialog = false }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Player Mode Selector (Solo vs AI, 2P, 3P, 4P office players)
                        Text(
                            text = "SELECT PLAYERS (OFFICE PASS & PLAY)",
                            color = uiState.theme.primaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        PlayerMode.entries.forEach { mode ->
                            val isSel = (uiState.playerMode == mode)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) uiState.theme.primaryColor.copy(alpha = 0.25f) else Color(0x221E293B))
                                    .border(if (isSel) 1.5.dp else 0.5.dp, if (isSel) uiState.theme.primaryColor else GlassBorderSubtle, RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.setPlayerMode(mode)
                                        showMenuDialog = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(mode.icon, fontSize = 18.sp)
                                        Column {
                                            Text(mode.displayName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(mode.description, color = TextSecondary, fontSize = 10.sp)
                                        }
                                    }
                                    if (isSel) {
                                        Text("ACTIVE ✓", color = uiState.theme.primaryColor, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Difficulty Selection
                        Text(
                            text = "BOARD DIFFICULTY (SNAKES & LADDERS)",
                            color = uiState.theme.primaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            SnakeDifficulty.entries.forEach { diff ->
                                val isSel = (uiState.difficulty == diff)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) uiState.theme.primaryColor else Color(0x331E293B))
                                        .clickable {
                                            viewModel.setDifficulty(diff)
                                            showMenuDialog = false
                                        }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = diff.displayName,
                                        color = if (isSel) Color.Black else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Board Graphic Style (if theme has board art)
                        if (uiState.theme.boardDrawableRes != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "BOARD DISPLAY STYLE",
                                color = uiState.theme.primaryColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (uiState.useArtBoard) uiState.theme.primaryColor else Color(0x331E293B))
                                        .clickable {
                                            if (!uiState.useArtBoard) viewModel.toggleArtBoard()
                                        }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "🖼️ Illustrated Art",
                                        color = if (uiState.useArtBoard) Color.Black else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (!uiState.useArtBoard) uiState.theme.primaryColor else Color(0x331E293B))
                                        .clickable {
                                            if (uiState.useArtBoard) viewModel.toggleArtBoard()
                                        }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "📐 Vector Grid",
                                        color = if (!uiState.useArtBoard) Color.Black else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons in Menu
                        NeonGradientButton(
                            text = "New Match",
                            onClick = {
                                viewModel.restartGame()
                                showMenuDialog = false
                            },
                            icon = Icons.Default.Replay,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Victory / Podium Dialog when Game Ends
        if (uiState.isGameOver) {
            Dialog(onDismissRequest = { /* force action */ }) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    backgroundColor = Color(0xF00D1527),
                    borderColor = GoldAccent
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏆 MATCH COMPLETED 🏆", color = GoldAccent, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Congratulations to the champions!", color = TextSecondary, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(14.dp))

                        // Standings Podium List
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val sortedStandings = (uiState.podium + uiState.players.filter { it.rank == null })
                            sortedStandings.forEachIndexed { index, player ->
                                val medal = when (player.rank ?: (index + 1)) {
                                    1 -> "🥇 1st Place"
                                    2 -> "🥈 2nd Place"
                                    3 -> "🥉 3rd Place"
                                    else -> "🎖️ 4th Place"
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x331E293B))
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(player.avatarIcon, fontSize = 14.sp)
                                        Text(player.name, color = player.color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(medal, color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        if (uiState.earnedCoins > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("+${uiState.earnedCoins} Gold Coins Earned! 💰", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        NeonGradientButton(
                            text = "Play Again",
                            onClick = { viewModel.restartGame() },
                            icon = Icons.Default.Replay,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Clean Canvas-rendered 3D Dice Dots
 */
@Composable
private fun DiceDotsView(dots: Int) {
    Canvas(modifier = Modifier.size(46.dp)) {
        val w = size.width
        val h = size.height
        val dotRadius = 4.2f
        val dotColor = Color(0xFF0F172A)

        val left = w * 0.25f
        val midX = w * 0.5f
        val right = w * 0.75f

        val top = h * 0.25f
        val midY = h * 0.5f
        val bottom = h * 0.75f

        when (dots) {
            1 -> {
                drawCircle(color = dotColor, radius = dotRadius * 1.3f, center = Offset(midX, midY))
            }
            2 -> {
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(left, top))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(right, bottom))
            }
            3 -> {
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(left, top))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(midX, midY))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(right, bottom))
            }
            4 -> {
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(left, top))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(right, top))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(left, bottom))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(right, bottom))
            }
            5 -> {
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(left, top))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(right, top))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(midX, midY))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(left, bottom))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(right, bottom))
            }
            6 -> {
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(left, top))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(right, top))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(left, midY))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(right, midY))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(left, bottom))
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(right, bottom))
            }
        }
    }
}
