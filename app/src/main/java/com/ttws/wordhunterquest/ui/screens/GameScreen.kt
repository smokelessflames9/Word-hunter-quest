package com.ttws.wordhunterquest.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttws.wordhunterquest.auth.findActivity
import com.ttws.wordhunterquest.data.CellPosition
import com.ttws.wordhunterquest.data.Level
import com.ttws.wordhunterquest.ui.components.BannerAdView
import com.ttws.wordhunterquest.ui.components.LocalResponsiveMetrics
import com.ttws.wordhunterquest.ui.components.WordGrid
import com.ttws.wordhunterquest.ui.components.tvFocusBorder
import com.ttws.wordhunterquest.ui.theme.CardNavy
import com.ttws.wordhunterquest.ui.theme.DeepNavy
import com.ttws.wordhunterquest.ui.theme.GridCellBorder
import com.ttws.wordhunterquest.ui.theme.MidnightBlue
import com.ttws.wordhunterquest.ui.theme.QuestAmber
import com.ttws.wordhunterquest.ui.theme.QuestCyan
import com.ttws.wordhunterquest.ui.theme.QuestEmerald
import com.ttws.wordhunterquest.ui.theme.QuestGold
import com.ttws.wordhunterquest.ui.theme.QuestGoldLight
import com.ttws.wordhunterquest.ui.theme.SlateNavy
import com.ttws.wordhunterquest.ui.theme.SurfaceDark
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
    val activity = context.findActivity()
    val metrics = LocalResponsiveMetrics.current

    BackHandler {
        onTogglePause()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .testTag("game_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Game Header (compact and bounded on ultra-wide tablets/TVs)
        GameHeaderBar(
            level = level,
            isCompactHeight = metrics.isCompactHeight,
            onBackToLevels = onBackToLevels,
            onRestart = onRestart,
            onTogglePause = onTogglePause,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 1200.dp)
        )

        if (metrics.useTwoPaneLayout) {
            // Two-Pane Adaptive Layout for Landscape Phones, Fire Tablets, Android Tablets, and Fire TV
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 1200.dp)
                    .padding(
                        horizontal = if (metrics.isTabletOrLarger) 20.dp else 12.dp,
                        vertical = if (metrics.isCompactHeight) 4.dp else 8.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(if (metrics.isTabletOrLarger) 20.dp else 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Pane: Responsive 1:1 Puzzle Board fitting 100% inside available space
                WordGrid(
                    grid = level.grid,
                    gridSize = level.gridSize,
                    discoveredWords = uiState.discoveredWords,
                    hintCells = uiState.hintCells,
                    isIncorrectFlash = uiState.isIncorrectFlash,
                    onWordSelected = onWordSelected,
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                )

                // Right Pane: Stats, Scrollable Word Chips, TV Controls Tip, and Hint Action
                Column(
                    modifier = Modifier
                        .weight(0.85f)
                        .fillMaxHeight()
                        .widthIn(max = 460.dp),
                    verticalArrangement = Arrangement.spacedBy(if (metrics.isCompactHeight) 6.dp else 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GameStatsBar(
                        discoveredCount = uiState.discoveredWords.size,
                        totalWords = level.words.size,
                        levelScore = uiState.levelScore,
                        persistentTotalScore = uiState.persistentTotalScore,
                        compact = metrics.isCompactHeight
                    )

                    TargetWordsCard(
                        level = level,
                        uiState = uiState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    if (metrics.isTv) {
                        Text(
                            text = "D-Pad: Move • OK: Start/Submit Word • Back: Cancel/Pause",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (activity != null) {
                        HintButton(
                            hintsRemaining = uiState.hintsRemaining,
                            isTv = metrics.isTv,
                            onClick = { onHintRequested(activity) }
                        )
                    }
                }
            }
        } else {
            // Single-Pane Adaptive Layout for Portrait Phones and Narrow Split-Screen Windows
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameStatsBar(
                    discoveredCount = uiState.discoveredWords.size,
                    totalWords = level.words.size,
                    levelScore = uiState.levelScore,
                    persistentTotalScore = uiState.persistentTotalScore,
                    compact = metrics.isCompactHeight,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Bounded Target Word List so it never pushes the puzzle grid off-screen
                val wordListMaxHeight = (metrics.maxHeight * 0.22f).coerceIn(84.dp, 175.dp)
                TargetWordsCard(
                    level = level,
                    uiState = uiState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = wordListMaxHeight)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Auto-fit Square Puzzle Board occupying all remaining space with zero clipping
                WordGrid(
                    grid = level.grid,
                    gridSize = level.gridSize,
                    discoveredWords = uiState.discoveredWords,
                    hintCells = uiState.hintCells,
                    isIncorrectFlash = uiState.isIncorrectFlash,
                    onWordSelected = onWordSelected,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (activity != null) {
                    HintButton(
                        hintsRemaining = uiState.hintsRemaining,
                        isTv = metrics.isTv,
                        onClick = { onHintRequested(activity) }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        // Responsive Adaptive Banner Ad (never shown on TV or cramped short landscape phones)
        if (!metrics.isCompactHeight && !metrics.isTv) {
            BannerAdView(modifier = Modifier.padding(bottom = 4.dp))
        }
    }

    // Toast message overlay
    uiState.toastMessage?.let { msg ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp, start = 24.dp, end = 24.dp),
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
                    color = Color.Black,
                    textAlign = TextAlign.Center
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
private fun GameHeaderBar(
    level: Level,
    isCompactHeight: Boolean,
    onBackToLevels: () -> Unit,
    onRestart: () -> Unit,
    onTogglePause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(
            horizontal = 12.dp,
            vertical = if (isCompactHeight) 2.dp else 6.dp
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBackToLevels,
            modifier = Modifier
                .tvFocusBorder(CircleShape)
                .testTag("game_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to Levels",
                tint = TextPrimary
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = "LEVEL ${level.id} • ${level.difficulty.uppercase()}",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = QuestGold,
                letterSpacing = 1.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = level.category,
                fontSize = if (isCompactHeight) 15.sp else 17.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = 1.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onRestart,
                modifier = Modifier
                    .tvFocusBorder(CircleShape)
                    .testTag("restart_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Restart Level",
                    tint = TextSecondary
                )
            }
            IconButton(
                onClick = onTogglePause,
                modifier = Modifier
                    .tvFocusBorder(CircleShape)
                    .testTag("pause_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause Game",
                    tint = TextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GameStatsBar(
    discoveredCount: Int,
    totalWords: Int,
    levelScore: Int,
    persistentTotalScore: Int,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val hPad = if (compact) 8.dp else 10.dp
        val vPad = if (compact) 4.dp else 5.dp

        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, SlateNavy)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = hPad, vertical = vPad),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WORDS: ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Text(
                    text = "$discoveredCount / $totalWords",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = QuestCyan
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, SlateNavy)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = hPad, vertical = vPad),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SCORE: ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Text(
                    text = "$levelScore",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = QuestGoldLight
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, QuestGold.copy(alpha = 0.6f)),
            modifier = Modifier.testTag("persistent_total_score_badge")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = hPad, vertical = vPad),
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
                    text = "$persistentTotalScore",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = QuestGoldLight
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TargetWordsCard(
    level: Level,
    uiState: GameUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GridCellBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
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
                            textDecoration = if (isFound) TextDecoration.LineThrough else TextDecoration.None,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HintButton(
    hintsRemaining: Int,
    isTv: Boolean,
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
            .tvFocusBorder(RoundedCornerShape(14.dp))
            .testTag("hint_button"),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (hasFreeHints || isTv) QuestGold else QuestCyan,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (hasFreeHints || isTv) Icons.Default.Lightbulb else Icons.Default.VideoLibrary,
                contentDescription = "Hint Icon",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when {
                    hasFreeHints -> "GET HINT ($hintsRemaining LEFT)"
                    isTv -> "REVEAL FREE HINT"
                    else -> "WATCH AD FOR FREE HINT"
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .tvFocusBorder(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = QuestGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("RESUME", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .tvFocusBorder(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = CardNavy, contentColor = TextPrimary),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SlateNavy)
                ) {
                    Text("RESTART LEVEL", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onExit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .tvFocusBorder(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = CardNavy, contentColor = TextSecondary),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SlateNavy)
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
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(QuestGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = QuestGold,
                        modifier = Modifier.size(34.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LEVEL $levelNumber COMPLETED!",
                    fontSize = 19.sp,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, QuestGold.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
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

                if (!isLastLevel) {
                    Button(
                        onClick = onNextLevel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .tvFocusBorder(RoundedCornerShape(14.dp))
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
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .tvFocusBorder(RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("REPLAY", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = onLevelsMenu,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .tvFocusBorder(RoundedCornerShape(12.dp)),
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
