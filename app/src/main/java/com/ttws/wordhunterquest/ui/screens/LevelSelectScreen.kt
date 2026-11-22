package com.ttws.wordhunterquest.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttws.wordhunterquest.data.LevelRepository
import com.ttws.wordhunterquest.ui.components.BannerAdView
import com.ttws.wordhunterquest.ui.components.LocalResponsiveMetrics
import com.ttws.wordhunterquest.ui.components.tvFocusBorder
import com.ttws.wordhunterquest.ui.theme.CardNavy
import com.ttws.wordhunterquest.ui.theme.GridCellBorder
import com.ttws.wordhunterquest.ui.theme.MidnightBlue
import com.ttws.wordhunterquest.ui.theme.QuestCyan
import com.ttws.wordhunterquest.ui.theme.QuestEmerald
import com.ttws.wordhunterquest.ui.theme.QuestGold
import com.ttws.wordhunterquest.ui.theme.QuestGoldLight
import com.ttws.wordhunterquest.ui.theme.TextMuted
import com.ttws.wordhunterquest.ui.theme.TextPrimary
import com.ttws.wordhunterquest.ui.theme.TextSecondary
import com.ttws.wordhunterquest.ui.viewmodel.GameUiState

@Composable
fun LevelSelectScreen(
    uiState: GameUiState,
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val levels = LevelRepository.getAllLevels()
    val metrics = LocalResponsiveMetrics.current

    // Adaptive column count: 3 on ultra-narrow, 4 on phones, 5 on tablets/landscape/Fire TV (exact 5x4 for 20 levels)
    val columns = when {
        metrics.maxWidth < 340.dp -> 3
        metrics.maxWidth < 600.dp -> 4
        else -> 5
    }
    val cardAspectRatio = if (metrics.useTwoPaneLayout || metrics.isLandscape) 1.25f else 0.92f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .testTag("level_select_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 1040.dp)
                .padding(horizontal = 16.dp, vertical = if (metrics.isCompactHeight) 6.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .tvFocusBorder(CircleShape)
                    .testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Text(
                text = "LEVEL SELECTION",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = QuestGoldLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardNavy)
                    .border(1.dp, QuestGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Score",
                    tint = QuestGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${uiState.persistentTotalScore}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuestGoldLight
                )
            }
        }

        // Subtitle & progress summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 1040.dp)
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "20 QUEST PUZZLES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Text(
                text = "COMPLETED: ${uiState.completedLevels.size}/20",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = QuestCyan
            )
        }

        // Adaptive Responsive Level Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 1040.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(levels) { level ->
                val isUnlocked = level.id <= uiState.highestUnlockedLevel
                val isCompleted = uiState.completedLevels.contains(level.id)
                val isCurrent = level.id == uiState.highestUnlockedLevel && !isCompleted

                LevelGridItem(
                    levelNumber = level.id,
                    category = level.category,
                    difficulty = level.difficulty,
                    isUnlocked = isUnlocked,
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    aspectRatio = cardAspectRatio,
                    onClick = {
                        if (isUnlocked) {
                            onLevelSelected(level.id)
                        }
                    }
                )
            }
        }

        if (!metrics.isCompactHeight && !metrics.isTv) {
            BannerAdView(modifier = Modifier.padding(bottom = 6.dp))
        }
    }
}

@Composable
private fun LevelGridItem(
    levelNumber: Int,
    category: String,
    difficulty: String,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    isCurrent: Boolean,
    aspectRatio: Float,
    onClick: () -> Unit
) {
    val borderColor = when {
        isCompleted -> QuestEmerald
        isCurrent -> QuestGold
        isUnlocked -> QuestCyan
        else -> GridCellBorder.copy(alpha = 0.5f)
    }

    val backgroundColor = when {
        isUnlocked -> CardNavy
        else -> MidnightBlue.copy(alpha = 0.6f)
    }

    val cardShape = RoundedCornerShape(16.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(cardShape)
            .border(
                width = if (isCurrent || isCompleted) 2.dp else 1.dp,
                color = borderColor,
                shape = cardShape
            )
            .tvFocusBorder(cardShape)
            .clickable(enabled = isUnlocked) { onClick() }
            .testTag("level_item_$levelNumber"),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = cardShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isUnlocked) {
                    Text(
                        text = "$levelNumber",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isCompleted) QuestEmerald else if (isCurrent) QuestGold else TextPrimary,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    if (isCompleted) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = QuestEmerald,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "DONE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = QuestEmerald,
                                maxLines = 1
                            )
                        }
                    } else if (isCurrent) {
                        Text(
                            text = "PLAY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = QuestGold,
                            maxLines = 1
                        )
                    } else {
                        Text(
                            text = difficulty,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked Level $levelNumber",
                        tint = TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "$levelNumber",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
