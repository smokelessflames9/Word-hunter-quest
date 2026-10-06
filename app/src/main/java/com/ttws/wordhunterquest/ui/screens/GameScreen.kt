package com.ttws.wordhunterquest.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttws.wordhunterquest.data.CellPosition
import com.ttws.wordhunterquest.ui.components.BannerAdView
import com.ttws.wordhunterquest.ui.components.WordGrid
import com.ttws.wordhunterquest.ui.theme.CardNavy
import com.ttws.wordhunterquest.ui.theme.DeepNavy
import com.ttws.wordhunterquest.ui.theme.GridCellBorder
import com.ttws.wordhunterquest.ui.theme.MidnightBlue
import com.ttws.wordhunterquest.ui.theme.QuestAmber
import com.ttws.wordhunterquest.ui.theme.QuestCyan
import com.ttws.wordhunterquest.ui.theme.QuestEmerald
import com.ttws.wordhunterquest.ui.theme.QuestGold
import com.ttws.wordhunterquest.ui.theme.QuestGoldLight
import com.ttws.wordhunterquest.ui.theme.QuestRuby
import com.ttws.wordhunterquest.ui.theme.SlateNavy
import com.ttws.wordhunterquest.ui.theme.SurfaceDark
import com.ttws.wordhunterquest.ui.theme.TextMuted
import com.ttws.wordhunterquest.ui.theme.TextPrimary
import com.ttws.wordhunterquest.ui.theme.TextSecondary
import com.ttws.wordhunterquest.ui.viewmodel.GameUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameScreen(
    uiState: GameUiState,
    onWordSelected: (String, List<CellPosition>) -> Unit,
    onHintRequested: (Activity) -> Unit,
    onRestart: () -> Unit,
    onTogglePause: () -> Unit,
    onNextLevel: (Activity) -> Unit,
    onBackToLevels: () -> Unit,
    modifier: Modifier = Modifier
) {
    val level = uiState.currentLevel ?: return
    val context = LocalContext.current
    val activity = context as? Activity

    BackHandler {
        onTogglePause()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .testTag("game_screen")
    ) {
        // Top Game Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackToLevels,
                modifier = Modifier.testTag("game_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Levels",
                    tint = TextPrimary
                )
            }

            // Level & Category Badge
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LEVEL ${level.id} • ${level.difficulty.uppercase()}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = QuestGold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = level.category,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.5.sp
                )
            }

            // Pause and Restart controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onRestart,
                    modifier = Modifier.testTag("restart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Level",
                        tint = TextSecondary
                    )
                }
                IconButton(
                    onClick = onTogglePause,
                    modifier = Modifier.testTag("pause_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause Game",
                        tint = TextSecondary
                    )
                }
            }
        }

        // Score Counters & Words Remaining Tracker Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Words Left Badge
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateNavy)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WORDS: ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Text(
                        text = "${uiState.discoveredWords.size} / ${level.words.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = QuestCyan
                    )
                }
            }

            // Level Score
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateNavy)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCORE: ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Text(
                        text = "${uiState.levelScore}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = QuestGoldLight
                    )
                }
            }

            // Persistent Total Score Counter (real-time updated)
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, QuestGold.copy(alpha = 0.6f)),
                modifier = Modifier.testTag("persistent_total_score_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Total Score Trophy",
                        tint = QuestGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${uiState.persistentTotalScore}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = QuestGoldLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Main Puzzle Area (Scrollable if needed on small phones)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Target Word List Chips
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GridCellBorder)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center
                    ) {
                        level.words.forEach { word ->
                            val isFound = uiState.discoveredWords.any { it.word == word }
                            val discoveredColor = uiState.discoveredWords.firstOrNull { it.word == word }?.color

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isFound) (discoveredColor ?: QuestEmerald).copy(alpha = 0.25f)
                                        else CardNavy
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isFound) (discoveredColor ?: QuestEmerald) else SlateNavy,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = word,
                                    fontSize = 12.sp,
                                    fontWeight = if (isFound) FontWeight.Normal else FontWeight.Bold,
                                    color = if (isFound) (discoveredColor ?: QuestEmerald) else TextPrimary,
                                    textDecoration = if (isFound) TextDecoration.LineThrough else TextDecoration.None
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // The Touch-and-Drag Word Grid
            WordGrid(
                grid = level.grid,
                gridSize = level.gridSize,
                discoveredWords = uiState.discoveredWords,
                hintCells = uiState.hintCells,
                isIncorrectFlash = uiState.isIncorrectFlash,
                onWordSelected = onWordSelected,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Hint Action Button
            if (activity != null) {
                HintButton(
                    hintsRemaining = uiState.hintsRemaining,
                    onClick = { onHintRequested(activity) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Real Banner Ad (anchored at bottom, never blocks game controls)
        BannerAdView(modifier = Modifier.padding(bottom = 4.dp))
    }

    // Toast message overlay
    uiState.toastMessage?.let { msg ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(QuestAmber)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = msg,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }

    // Pause Dialog
    if (uiState.isPaused) {
        PauseDialog(
            onResume = onTogglePause,
            onRestart = onRestart,
            onExit = onBackToLevels
        )
    }

    // Level Completed Dialog
    if (uiState.isLevelCompleted && activity != null) {
        LevelCompletedDialog(
            levelNumber = level.id,
            category = level.category,
            levelScore = uiState.levelScore,
            totalScore = uiState.persistentTotalScore,
            isLastLevel = level.id >= 20,
            onNextLevel = { onNextLevel(activity) },
            onReplay = onRestart,
            onLevelsMenu = onBackToLevels
        )
    }
}

@Composable
private fun HintButton(
    hintsRemaining: Int,
    onClick: () -> Unit
) {
    val hasFreeHints = hintsRemaining > 0

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 380.dp)
            .height(48.dp)
            .shadow(6.dp, RoundedCornerShape(14.dp))
            .testTag("hint_button"),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (hasFreeHints) QuestGold else QuestCyan,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (hasFreeHints) Icons.Default.Lightbulb else Icons.Default.VideoLibrary,
                contentDescription = "Hint Icon",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (hasFreeHints) {
                Text(
                    text = "GET HINT ($hintsRemaining LEFT)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            } else {
                Text(
                    text = "WATCH AD FOR FREE HINT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onResume,
        title = {
            Text(
                text = "GAME PAUSED",
                fontWeight = FontWeight.Black,
                color = QuestGoldLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = QuestGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("RESUME", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CardNavy, contentColor = TextPrimary),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateNavy)
                ) {
                    Text("RESTART LEVEL", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CardNavy, contentColor = TextSecondary),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateNavy)
                ) {
                    Text("LEVEL SELECTION", fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {},
        containerColor = DeepNavy,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun LevelCompletedDialog(
    levelNumber: Int,
    category: String,
    levelScore: Int,
    totalScore: Int,
    isLastLevel: Boolean,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onLevelsMenu: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(QuestGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = QuestGold,
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "LEVEL $levelNumber COMPLETED!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = QuestGoldLight,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = category,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuestCyan,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Score Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, QuestGold.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LEVEL SCORE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Text(
                            text = "+$levelScore PTS",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = QuestEmerald
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "TOTAL QUEST SCORE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Text(
                            text = "$totalScore PTS",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = QuestGoldLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Next Level Button
                if (!isLastLevel) {
                    Button(
                        onClick = onNextLevel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("next_level_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = QuestGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NEXT LEVEL",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                } else {
                    Text(
                        text = "CONGRATULATIONS! ALL 20 LEVELS COMPLETED!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = QuestGoldLight,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onReplay,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("REPLAY", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = onLevelsMenu,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("LEVELS", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = DeepNavy,
        shape = RoundedCornerShape(20.dp)
    )
}
