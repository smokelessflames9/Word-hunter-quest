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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttws.wordhunterquest.data.LevelRepository
import com.ttws.wordhunterquest.ui.components.BannerAdView
import com.ttws.wordhunterquest.ui.theme.CardNavy
import com.ttws.wordhunterquest.ui.theme.GridCellBorder
import com.ttws.wordhunterquest.ui.theme.MidnightBlue
import com.ttws.wordhunterquest.ui.theme.QuestCyan
import com.ttws.wordhunterquest.ui.theme.QuestEmerald
import com.ttws.wordhunterquest.ui.theme.QuestGold
import com.ttws.wordhunterquest.ui.theme.QuestGoldLight
import com.ttws.wordhunterquest.ui.theme.SlateNavy
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .testTag("level_select_screen")
    ) {
        // Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("back_button")
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
                color = QuestGoldLight
            )

            // Persistent Total Score Counter Badge
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

        // Subtitle & difficulty legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
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

        // 4 Columns Grid (5 rows of 4 or 5 columns) -> 4 columns gives large, touch-friendly cells
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
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
                    onClick = {
                        if (isUnlocked) {
                            onLevelSelected(level.id)
                        }
                    }
                )
            }
        }

        // Banner Ad
        BannerAdView(modifier = Modifier.padding(bottom = 6.dp))
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
    onClick: () -> Unit
) {
    val borderColor = when {
        isCompleted -> QuestEmerald
        isCurrent -> QuestGold
        isUnlocked -> QuestCyan
        else -> GridCellBorder.copy(alpha = 0.5f)
    }

    val backgroundColor = when {
        isCompleted -> CardNavy
        isCurrent -> CardNavy
        isUnlocked -> CardNavy
        else -> MidnightBlue.copy(alpha = 0.6f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isCurrent || isCompleted) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = isUnlocked) { onClick() }
            .testTag("level_item_$levelNumber"),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isUnlocked) {
                    // Level Number
                    Text(
                        text = "$levelNumber",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isCompleted) QuestEmerald else if (isCurrent) QuestGold else TextPrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Category
                    Text(
                        text = category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Status Badge (Star / Check)
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
                                color = QuestEmerald
                            )
                        }
                    } else if (isCurrent) {
                        Text(
                            text = "PLAY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = QuestGold
                        )
                    } else {
                        Text(
                            text = difficulty,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextMuted
                        )
                    }
                } else {
                    // Locked Level
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked Level $levelNumber",
                        tint = TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$levelNumber",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
