package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GlowGradient
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldGradient
import com.example.ui.theme.TextPrimary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = GlassSurface,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    testTag: String = "glass_card",
    content: @Composable BoxScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .testTag(testTag)
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .then(clickModifier),
        content = content
    )
}

@Composable
fun NeonGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    gradient: Brush = GlowGradient,
    enabled: Boolean = true,
    testTag: String = "neon_button"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(if (enabled) gradient else Brush.linearGradient(listOf(Color(0xFF334155), Color(0xFF1E293B))))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Box(modifier = Modifier.size(8.dp))
            }
            Text(
                text = text,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GlassOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tintColor: Color = CyanAccent,
    testTag: String = "glass_outline_button"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(GlassSurface)
            .border(1.dp, tintColor.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier.size(18.dp)
                )
                Box(modifier = Modifier.size(6.dp))
            }
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun StarRatingRow(
    stars: Int,
    maxStars: Int = 3,
    starSize: Dp = 26.dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            if (i <= stars) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Earned Star",
                    tint = GoldAccent,
                    modifier = Modifier.size(starSize)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.StarOutline,
                    contentDescription = "Empty Star",
                    tint = Color(0x55FBBF24),
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}

@Composable
fun CoinBadge(
    coins: Int,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val clickMod = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xD91F293D))
            .border(1.dp, Color(0x66F59E0B), RoundedCornerShape(20.dp))
            .then(clickMod)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(GoldGradient),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "¢",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }
            Text(
                text = "$coins",
                color = GoldAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
