package com.example.ui.photopuzzle

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.BorderOuter
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GamePreferences
import com.example.data.PuzzleDifficulty
import com.example.data.PuzzlePiece
import com.example.ui.components.CoinBadge
import com.example.ui.components.LevelCompleteDialog
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlowGradient
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoseAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

@Composable
fun PhotoPuzzleScreen(
    viewModel: PhotoPuzzleViewModel,
    preferences: GamePreferences,
    levelNumber: Int,
    difficulty: PuzzleDifficulty,
    customUri: String? = null,
    onBackToHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showGhostSlider by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.startLevel(uiState.levelNumber, uiState.difficulty, uri.toString())
        }
    }

    LaunchedEffect(levelNumber, difficulty, customUri) {
        viewModel.startLevel(levelNumber, difficulty, customUri)
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
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .widthIn(max = 600.dp)
                .align(Alignment.TopCenter)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToHub,
                    modifier = Modifier.testTag("photo_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Level ${uiState.levelNumber} • ${uiState.difficulty.name}",
                        color = CyanAccent,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (uiState.customImageUri != null) "Custom Photo" else uiState.category.displayName,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                CoinBadge(
                    coins = preferences.coins,
                    modifier = Modifier.testTag("photo_coins_badge")
                )
            }

            // Status Bar (Timer & Custom HD Photo button)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isCountdown = uiState.difficulty.defaultTimerSec > 0
                val displaySec = if (isCountdown) uiState.remainingTimeSec else uiState.elapsedSec
                val m = displaySec / 60
                val s = displaySec % 60
                val timerColor = if (isCountdown && displaySec <= 30) RoseAccent else CyanAccent

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassSurface)
                        .border(1.dp, timerColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(if (isCountdown) "⏱ " else "⏳ ", fontSize = 12.sp)
                        Text(
                            text = String.format("%02d:%02d", m, s),
                            color = timerColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (uiState.isTimerFrozen) {
                            Text("❄️", fontSize = 11.sp)
                        }
                    }
                }

                // Add Custom HD Photo Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassSurface)
                        .border(1.dp, VioletAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("add_custom_photo_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = VioletAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Add HD Photo",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Lifted Piece Guidance Banner ("Pic Uthe" Indicator)
            val selected = uiState.selectedPiece
            if (selected != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x3338BDF8))
                        .border(1.dp, CyanAccent, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.PanTool, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                            Text("Piece #${selected.id + 1} Lifted! Tap a slot to place", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Drop ⤵",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.selectPiece(selected) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Main Puzzle Canvas Area
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF080C17))
                    .border(2.dp, GlassBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                val rows = uiState.difficulty.rows
                val cols = uiState.difficulty.cols

                if (uiState.isLoadingImage) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = CyanAccent, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Slicing Clean HD Photo...", color = TextSecondary, fontSize = 13.sp)
                    }
                } else {
                    // Ghost background preview
                    uiState.fullBitmap?.let { bmp ->
                        if (uiState.ghostOpacity > 0f) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Ghost Guide",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(uiState.ghostOpacity),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    // 1. Authentic Jigsaw Interlocking Cut Layer & Placed Pieces
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val boardW = size.width
                        val boardH = size.height
                        val cellW = boardW / cols
                        val cellH = boardH / rows
                        val tabSize = maxOf(cellW, cellH) * 0.22f

                        val nativeCanvas = drawContext.canvas.nativeCanvas

                        // A. Draw authentic Jigsaw puzzle cut outlines for empty slots
                        val outlinePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            style = android.graphics.Paint.Style.STROKE
                            strokeWidth = 2f
                            color = android.graphics.Color.argb(85, 56, 189, 248) // Frosted cyan puzzle cut
                        }

                        val selectedSlotPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            style = android.graphics.Paint.Style.STROKE
                            strokeWidth = 3.5f
                            color = android.graphics.Color.argb(230, 56, 189, 248) // Glowing lifted target highlight
                        }

                        val selectedSlotFill = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                            style = android.graphics.Paint.Style.FILL
                            color = android.graphics.Color.argb(45, 56, 189, 248) // Translucent glow inside slot
                        }

                        for (r in 0 until rows) {
                            for (c in 0 until cols) {
                                val slotIdx = r * cols + c
                                val isOccupied = uiState.boardSlots.containsKey(slotIdx)
                                val template = uiState.slotTemplates[slotIdx]

                                if (template != null && !isOccupied) {
                                    val path = com.example.util.ImageSlicer.createJigsawPath(
                                        cellW = cellW,
                                        cellH = cellH,
                                        pad = 0f,
                                        topTab = template.topTab,
                                        rightTab = template.rightTab,
                                        bottomTab = template.bottomTab,
                                        leftTab = template.leftTab
                                    )
                                    val matrix = android.graphics.Matrix()
                                    matrix.postTranslate(c * cellW, r * cellH)
                                    path.transform(matrix)

                                    val curSel = uiState.selectedPiece
                                    val isTarget = (curSel != null && curSel.correctIndex == slotIdx)
                                    if (isTarget) {
                                        nativeCanvas.drawPath(path, selectedSlotFill)
                                        nativeCanvas.drawPath(path, selectedSlotPaint)
                                    } else {
                                        nativeCanvas.drawPath(path, outlinePaint)
                                    }
                                }
                            }
                        }

                        // B. Draw placed pieces (cut in authentic jigsaw shape with tabs & blanks!)
                        for ((slotIdx, piece) in uiState.boardSlots) {
                            val r = slotIdx / cols
                            val c = slotIdx % cols
                            val destLeft = c * cellW - tabSize
                            val destTop = r * cellH - tabSize
                            val destW = cellW + 2 * tabSize
                            val destH = cellH + 2 * tabSize

                            drawImage(
                                image = piece.bitmap.asImageBitmap(),
                                dstOffset = androidx.compose.ui.unit.IntOffset(destLeft.toInt(), destTop.toInt()),
                                dstSize = androidx.compose.ui.unit.IntSize(destW.toInt(), destH.toInt())
                            )
                        }
                    }

                    // 2. Interactive Touch Grid (No square borders — pure jigsaw shapes)
                    Column(modifier = Modifier.fillMaxSize()) {
                        for (r in 0 until rows) {
                            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                for (c in 0 until cols) {
                                    val slotIndex = r * cols + c
                                    val isOccupied = uiState.boardSlots.containsKey(slotIndex)

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                            .clickable {
                                                viewModel.onSlotClicked(slotIndex)
                                            }
                                            .testTag("puzzle_slot_$slotIndex"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!isOccupied) {
                                            // Empty slot locator center dot
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0x3338BDF8))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Toolbar (Ghost, Border filter, Rotate, Magnet hint, Freeze)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showGhostSlider = !showGhostSlider },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (uiState.ghostOpacity > 0f) CyanAccent.copy(alpha = 0.2f) else GlassSurface)
                        .border(1.dp, GlassBorder, CircleShape)
                        .testTag("ghost_toggle_button")
                ) {
                    Icon(
                        imageVector = if (uiState.ghostOpacity > 0f) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Ghost Image",
                        tint = if (uiState.ghostOpacity > 0f) CyanAccent else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleEdgeFilter() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (uiState.edgeOnlyFilter) VioletAccent.copy(alpha = 0.2f) else GlassSurface)
                        .border(1.dp, GlassBorder, CircleShape)
                        .testTag("edge_filter_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BorderOuter,
                        contentDescription = "Filter Edge Pieces",
                        tint = if (uiState.edgeOnlyFilter) VioletAccent else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Rotate piece button (rotates the lifted piece!)
                IconButton(
                    onClick = { viewModel.rotateSelectedPiece() },
                    enabled = uiState.selectedPiece != null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (uiState.selectedPiece != null) EmeraldAccent.copy(alpha = 0.25f) else GlassSurface)
                        .border(1.dp, if (uiState.selectedPiece != null) EmeraldAccent else GlassBorderSubtle, CircleShape)
                        .testTag("rotate_piece_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RotateRight,
                        contentDescription = "Rotate Piece 90°",
                        tint = if (uiState.selectedPiece != null) EmeraldAccent else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (uiState.difficulty.defaultTimerSec > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(GlassSurface)
                            .border(1.dp, CyanAccent.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                            .clickable { viewModel.applyTimeFreeze() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("time_freeze_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.AcUnit, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(14.dp))
                            Text("+30s", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xD91E293B))
                        .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .clickable { viewModel.applyMagnetHint() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("magnet_hint_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                        Text("Magnet (30¢)", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            AnimatedVisibility(visible = showGhostSlider) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassSurface)
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Guide", color = TextSecondary, fontSize = 11.sp)
                    Slider(
                        value = uiState.ghostOpacity,
                        onValueChange = { viewModel.setGhostOpacity(it) },
                        valueRange = 0f..0.5f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = Color(0x3338BDF8)
                        )
                    )
                    Text("${(uiState.ghostOpacity * 200).toInt()}%", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Pieces Tray Carousel with 3D "Pic Uthe" Lift Animation
            val visiblePieces = if (uiState.edgeOnlyFilter) {
                uiState.pieces.filter { it.isEdge }
            } else {
                uiState.pieces
            }

            Text(
                text = "PUZZLE PIECES (${visiblePieces.size} in tray • Tap to pick up/lift)",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
            )

            if (visiblePieces.isEmpty() && uiState.pieces.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GlassSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text("All border pieces placed! Disable filter to view inner pieces.", color = TextSecondary, fontSize = 12.sp)
                }
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x5511192C))
                        .border(1.dp, GlassBorderSubtle, RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(visiblePieces, key = { it.id }) { piece ->
                        val isSelected = uiState.selectedPiece?.id == piece.id

                        // 3D "Pic Uthe" animation parameters:
                        val animatedScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.18f else 1.0f,
                            animationSpec = spring(dampingRatio = 0.7f),
                            label = "pieceScale"
                        )
                        val animatedElevation by animateDpAsState(
                            targetValue = if (isSelected) 16.dp else 2.dp,
                            label = "pieceElevation"
                        )
                        val animatedOffsetY by animateDpAsState(
                            targetValue = if (isSelected) (-8).dp else 0.dp,
                            label = "pieceOffsetY"
                        )

                        Box(
                            modifier = Modifier
                                .offset(y = animatedOffsetY)
                                .scale(animatedScale)
                                .size(82.dp)
                                .clickable { viewModel.selectPiece(piece) }
                                .testTag("piece_item_${piece.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                // Glowing organic aura behind lifted piece
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(CircleShape)
                                        .background(CyanAccent.copy(alpha = 0.35f))
                                )
                            }
                            // Authentic Jigsaw Cutout Piece (Real tabs and blanks!)
                            Image(
                                bitmap = piece.bitmap.asImageBitmap(),
                                contentDescription = "Piece ${piece.id}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )

                            // "LIFTED / UTHA HUA" Badge
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 1.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyanAccent)
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text("LIFTED 👆", color = Color(0xFF0F172A), fontSize = 8.sp, fontWeight = FontWeight.Black)
                                }
                            }

                            if (piece.rotation != 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xCC000000))
                                        .padding(horizontal = 3.dp, vertical = 1.dp)
                                ) {
                                    Text("${piece.rotation}°", color = GoldAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
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
                    viewModel.startLevel(nextLvl, uiState.difficulty, uiState.customImageUri)
                },
                onReplay = {
                    viewModel.startLevel(uiState.levelNumber, uiState.difficulty, uiState.customImageUri)
                },
                onDismiss = {
                    onBackToHub()
                }
            )
        }
    }
}
