package com.ttws.wordhunterquest.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttws.wordhunterquest.ui.components.BannerAdView
import com.ttws.wordhunterquest.ui.components.GameLogo
import com.ttws.wordhunterquest.ui.theme.CardNavy
import com.ttws.wordhunterquest.ui.theme.MidnightBlue
import com.ttws.wordhunterquest.ui.theme.QuestAmber
import com.ttws.wordhunterquest.ui.theme.QuestCyan
import com.ttws.wordhunterquest.ui.theme.QuestGold
import com.ttws.wordhunterquest.ui.theme.QuestGoldLight
import com.ttws.wordhunterquest.ui.theme.SlateNavy
import com.ttws.wordhunterquest.ui.theme.TextPrimary
import com.ttws.wordhunterquest.ui.theme.TextSecondary
import com.ttws.wordhunterquest.ui.viewmodel.GameUiState
import com.ttws.wordhunterquest.ui.viewmodel.Screen

@Composable
fun HomeScreen(
    uiState: GameUiState,
    onNavigate: (Screen) -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .testTag("home_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Game Logo
            GameLogo(
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Persistent Total Score Badge
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .testTag("persistent_score_card"),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, QuestGold.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(QuestGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Trophy Icon",
                                tint = QuestGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "TOTAL SCORE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${uiState.persistentTotalScore} PTS",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = QuestGoldLight
                            )
                        }
                    }

                    // Levels Completed counter
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "PROGRESS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${uiState.completedLevels.size} / 20",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = QuestCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // PLAY Button (Hero button)
            Button(
                onClick = onPlay,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .height(58.dp)
                    .shadow(10.dp, RoundedCornerShape(16.dp))
                    .testTag("play_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = QuestGold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Icon",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // LEVELS Button
            MenuButton(
                text = "LEVELS",
                icon = Icons.AutoMirrored.Filled.List,
                accentColor = QuestCyan,
                testTag = "levels_button",
                onClick = { onNavigate(Screen.LEVEL_SELECT) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // SETTINGS Button
            MenuButton(
                text = "SETTINGS",
                icon = Icons.Default.Settings,
                accentColor = SlateNavy,
                testTag = "settings_button",
                onClick = { onNavigate(Screen.SETTINGS) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ABOUT Button
            MenuButton(
                text = "ABOUT",
                icon = Icons.Default.Info,
                accentColor = SlateNavy,
                testTag = "about_button",
                onClick = { onNavigate(Screen.ABOUT) }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Real AdMob Banner Ad at bottom (never covers interactive controls)
        BannerAdView(modifier = Modifier.padding(bottom = 6.dp))
    }
}

@Composable
private fun MenuButton(
    text: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp)
            .height(52.dp)
            .testTag(testTag),
        colors = ButtonDefaults.buttonColors(
            containerColor = CardNavy,
            contentColor = TextPrimary
        ),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateNavy)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$text Icon",
                tint = if (accentColor == SlateNavy) TextSecondary else accentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}
