package com.ttws.wordhunterquest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttws.wordhunterquest.ui.components.BannerAdView
import com.ttws.wordhunterquest.ui.components.GameLogo
import com.ttws.wordhunterquest.ui.components.LocalResponsiveMetrics
import com.ttws.wordhunterquest.ui.components.OnboardingTutorialDialog
import com.ttws.wordhunterquest.ui.components.rateApp
import com.ttws.wordhunterquest.ui.components.shareApp
import com.ttws.wordhunterquest.ui.components.tvFocusBorder
import com.ttws.wordhunterquest.ui.theme.CardNavy
import com.ttws.wordhunterquest.ui.theme.MidnightBlue
import com.ttws.wordhunterquest.ui.theme.QuestCyan
import com.ttws.wordhunterquest.ui.theme.QuestEmerald
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
    onShowTutorial: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val metrics = LocalResponsiveMetrics.current

    if (uiState.showTutorialDialog) {
        OnboardingTutorialDialog(
            onDismiss = { onShowTutorial(false) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .testTag("home_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (metrics.useTwoPaneLayout) {
            // Two-Pane Hero Layout for Landscape Phones, Fire Tablets, Android Tablets, and Fire TV
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 1100.dp)
                    .padding(
                        horizontal = if (metrics.isTabletOrLarger) 32.dp else 20.dp,
                        vertical = if (metrics.isCompactHeight) 8.dp else 16.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(if (metrics.isTabletOrLarger) 36.dp else 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Pane: Game Logo & Persistent Progress Summary
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    GameLogo(
                        compact = metrics.isCompactHeight,
                        modifier = Modifier.padding(bottom = if (metrics.isCompactHeight) 10.dp else 18.dp)
                    )
                    PersistentScoreCard(uiState = uiState)
                }

                // Right Pane: Action Menu Buttons
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    HomeActionMenu(
                        onPlay = onPlay,
                        onNavigate = onNavigate,
                        onShowTutorial = onShowTutorial,
                        onShare = { shareApp(context) },
                        onRate = { rateApp(context) },
                        compact = metrics.isCompactHeight,
                        showSocialRow = !metrics.isTv
                    )
                }
            }
        } else {
            // Single-Pane Layout for Portrait Phones and Narrow Windows
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                GameLogo(modifier = Modifier.padding(bottom = 16.dp))

                PersistentScoreCard(uiState = uiState)

                Spacer(modifier = Modifier.height(22.dp))

                HomeActionMenu(
                    onPlay = onPlay,
                    onNavigate = onNavigate,
                    onShowTutorial = onShowTutorial,
                    onShare = { shareApp(context) },
                    onRate = { rateApp(context) },
                    compact = false,
                    showSocialRow = !metrics.isTv
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (!metrics.isCompactHeight && !metrics.isTv) {
            BannerAdView(modifier = Modifier.padding(bottom = 6.dp))
        }
    }
}

@Composable
private fun PersistentScoreCard(
    uiState: GameUiState
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp)
            .testTag("persistent_score_card"),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, QuestGold.copy(alpha = 0.6f))
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
}

@Composable
private fun HomeActionMenu(
    onPlay: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onShowTutorial: (Boolean) -> Unit,
    onShare: () -> Unit,
    onRate: () -> Unit,
    compact: Boolean,
    showSocialRow: Boolean
) {
    val itemSpacing = if (compact) 8.dp else 12.dp
    val buttonHeight = if (compact) 48.dp else 50.dp

    Button(
        onClick = onPlay,
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp)
            .height(if (compact) 50.dp else 56.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .tvFocusBorder(RoundedCornerShape(16.dp))
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

    Spacer(modifier = Modifier.height(itemSpacing))

    MenuButton(
        text = "LEVELS",
        icon = Icons.AutoMirrored.Filled.List,
        accentColor = QuestCyan,
        testTag = "levels_button",
        height = buttonHeight,
        onClick = { onNavigate(Screen.LEVEL_SELECT) }
    )

    Spacer(modifier = Modifier.height(itemSpacing))

    MenuButton(
        text = "HOW TO PLAY",
        icon = Icons.AutoMirrored.Filled.HelpOutline,
        accentColor = QuestEmerald,
        testTag = "how_to_play_button",
        height = buttonHeight,
        onClick = { onShowTutorial(true) }
    )

    Spacer(modifier = Modifier.height(itemSpacing))

    MenuButton(
        text = "SETTINGS",
        icon = Icons.Default.Settings,
        accentColor = SlateNavy,
        testTag = "settings_button",
        height = buttonHeight,
        onClick = { onNavigate(Screen.SETTINGS) }
    )

    Spacer(modifier = Modifier.height(itemSpacing))

    MenuButton(
        text = "ABOUT",
        icon = Icons.Default.Info,
        accentColor = SlateNavy,
        testTag = "about_button",
        height = buttonHeight,
        onClick = { onNavigate(Screen.ABOUT) }
    )

    if (showSocialRow) {
        Spacer(modifier = Modifier.height(itemSpacing + 2.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onShare,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .tvFocusBorder(RoundedCornerShape(14.dp))
                    .testTag("share_app_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, QuestCyan.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = CardNavy,
                    contentColor = TextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share App",
                    tint = QuestCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SHARE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            OutlinedButton(
                onClick = onRate,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .tvFocusBorder(RoundedCornerShape(14.dp))
                    .testTag("rate_app_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, QuestGold.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = CardNavy,
                    contentColor = TextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rate App",
                    tint = QuestGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RATE US",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    height: androidx.compose.ui.unit.Dp = 50.dp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp)
            .height(height)
            .tvFocusBorder(RoundedCornerShape(14.dp))
            .testTag(testTag),
        colors = ButtonDefaults.buttonColors(
            containerColor = CardNavy,
            contentColor = TextPrimary
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SlateNavy)
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
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
