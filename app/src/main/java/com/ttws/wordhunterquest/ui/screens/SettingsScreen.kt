package com.ttws.wordhunterquest.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttws.wordhunterquest.ui.components.BannerAdView
import com.ttws.wordhunterquest.ui.theme.CardNavy
import com.ttws.wordhunterquest.ui.theme.DeepNavy
import com.ttws.wordhunterquest.ui.theme.MidnightBlue
import com.ttws.wordhunterquest.ui.theme.QuestCyan
import com.ttws.wordhunterquest.ui.theme.QuestGold
import com.ttws.wordhunterquest.ui.theme.QuestGoldLight
import com.ttws.wordhunterquest.ui.theme.QuestRuby
import com.ttws.wordhunterquest.ui.theme.SlateNavy
import com.ttws.wordhunterquest.ui.theme.TextMuted
import com.ttws.wordhunterquest.ui.theme.TextPrimary
import com.ttws.wordhunterquest.ui.theme.TextSecondary
import com.ttws.wordhunterquest.ui.viewmodel.GameUiState
import com.ttws.wordhunterquest.ui.viewmodel.Screen

@Composable
fun SettingsScreen(
    uiState: GameUiState,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    onShowResetConfirmation: (Boolean) -> Unit,
    onConfirmReset: () -> Unit,
    onSignOut: () -> Unit,
    onSignInRequested: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .testTag("settings_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SETTINGS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = QuestGoldLight
            )
        }

        // Settings items
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Google Account & Cloud Sync Section
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (uiState.isSignedIn) QuestGold.copy(alpha = 0.5f) else SlateNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ACCOUNT & CLOUD BACKUP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuestCyan,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (uiState.isSignedIn) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiState.userDisplayName ?: "Explorer",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = uiState.userEmail ?: "Signed in with Google",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Cloud Sync: Active (Firestore)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = com.ttws.wordhunterquest.ui.theme.QuestEmerald
                                )
                            }
                            OutlinedButton(
                                onClick = onSignOut,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("sign_out_button")
                            ) {
                                Text("SIGN OUT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Guest Player (Offline)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Connect Google account to backup progress",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            Button(
                                onClick = onSignInRequested,
                                colors = ButtonDefaults.buttonColors(containerColor = QuestGold, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("sign_in_settings_button")
                            ) {
                                Text("SIGN IN", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
            // Audio & Haptic Section
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AUDIO & HAPTICS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuestCyan,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sound Toggle
                    SettingToggleRow(
                        title = "Sound Effects",
                        subtitle = "Game sounds, fanfares, and chimes",
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        checked = uiState.soundEnabled,
                        onCheckedChange = { onToggleSound() },
                        testTag = "sound_toggle"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vibration Toggle
                    SettingToggleRow(
                        title = "Vibration Feedback",
                        subtitle = "Haptic pulses when discovering words",
                        icon = Icons.Default.Vibration,
                        checked = uiState.vibrationEnabled,
                        onCheckedChange = { onToggleVibration() },
                        testTag = "vibration_toggle"
                    )
                }
            }

            // Game Progress Section
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DATA & PROGRESS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuestCyan,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Reset Progress Item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onShowResetConfirmation(true) }
                            .padding(vertical = 10.dp, horizontal = 8.dp)
                            .testTag("reset_progress_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LockReset,
                                contentDescription = "Reset Progress",
                                tint = QuestRuby,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Reset All Progress",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = QuestRuby
                                )
                                Text(
                                    text = "Lock levels, reset high score and hints",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Legal & About Section
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INFORMATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuestCyan,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Privacy Policy
                    SettingActionRow(
                        title = "Privacy Policy",
                        subtitle = "AdMob and device data disclosure",
                        icon = Icons.Default.PrivacyTip,
                        onClick = { showPrivacyPolicyDialog = true },
                        testTag = "privacy_policy_button"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // About
                    SettingActionRow(
                        title = "About Word Hunter Quest",
                        subtitle = "Version, credits and developer info",
                        icon = Icons.Default.Info,
                        onClick = { onNavigate(Screen.ABOUT) },
                        testTag = "about_settings_button"
                    )
                }
            }
        }

        // Bottom Banner Ad
        BannerAdView(modifier = Modifier.padding(bottom = 6.dp))
    }

    // Reset Progress Confirmation Dialog
    if (uiState.showResetConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { onShowResetConfirmation(false) },
            title = {
                Text(
                    text = "Reset All Progress?",
                    fontWeight = FontWeight.Black,
                    color = QuestRuby
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to reset all game progress? This will reset your completed levels, total score counter, and hints.",
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmReset,
                    colors = ButtonDefaults.buttonColors(containerColor = QuestRuby, contentColor = Color.White),
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text("RESET", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { onShowResetConfirmation(false) },
                    modifier = Modifier.testTag("cancel_reset_button")
                ) {
                    Text("CANCEL", color = TextPrimary)
                }
            },
            containerColor = DeepNavy,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = {
                Text(
                    text = "Privacy Policy",
                    fontWeight = FontWeight.Black,
                    color = QuestGoldLight
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "WORD HUNTER QUEST is developed by Thomas Tech WS.\n\n" +
                                "1. Local Storage: Your game progress, unlocked levels, scores, and settings are saved strictly locally on your device.\n\n" +
                                "2. Advertising: The app uses Google AdMob to display banner, interstitial, and rewarded advertisements for hints. AdMob may process non-personally identifiable diagnostic and advertising identifiers in compliance with Google Play Policy.\n\n" +
                                "3. Offline Support: Word Hunter Quest does not require an account or internet connectivity for core word puzzle gameplay.",
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyPolicyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = QuestGold, contentColor = Color.Black)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DeepNavy,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = QuestGold,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = QuestGold,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = SlateNavy
            )
        )
    }
}

@Composable
private fun SettingActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = QuestCyan,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}
