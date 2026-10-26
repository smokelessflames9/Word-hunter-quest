package com.ttws.wordhunterquest.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ttws.wordhunterquest.ui.theme.CardNavy
import com.ttws.wordhunterquest.ui.theme.DeepNavy
import com.ttws.wordhunterquest.ui.theme.QuestCyan
import com.ttws.wordhunterquest.ui.theme.QuestEmerald
import com.ttws.wordhunterquest.ui.theme.QuestGold
import com.ttws.wordhunterquest.ui.theme.QuestGoldLight
import com.ttws.wordhunterquest.ui.theme.SlateNavy
import com.ttws.wordhunterquest.ui.theme.TextPrimary
import com.ttws.wordhunterquest.ui.theme.TextSecondary

/**
 * Launches the system Share Sheet with the app's Google Play Store URL.
 */
fun shareApp(context: Context) {
    val playStoreUrl = "https://play.google.com/store/apps/details?id=${context.packageName}"
    val shareMessage = "Play WORD HUNTER QUEST! Search and discover hidden words across 20 themed puzzle levels:\n$playStoreUrl"
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "WORD HUNTER QUEST")
        putExtra(Intent.EXTRA_TEXT, shareMessage)
    }
    val chooser = Intent.createChooser(sendIntent, "Share WORD HUNTER QUEST").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(chooser)
    } catch (_: Exception) {}
}

/**
 * Opens the app's Google Play Store listing for rating/reviewing, with browser fallback.
 */
fun rateApp(context: Context) {
    val packageName = context.packageName
    val marketIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$packageName")
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(marketIntent)
    } catch (_: ActivityNotFoundException) {
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(webIntent)
        } catch (_: Exception) {}
    }
}

/**
 * Interactive 3-step Onboarding / How to Play Walkthrough Dialog.
 */
@Composable
fun OnboardingTutorialDialog(
    onDismiss: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    val totalSteps = 3

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            shape = RoundedCornerShape(22.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, QuestGold.copy(alpha = 0.7f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_tutorial_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Row with Skip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HOW TO PLAY (${step + 1}/$totalSteps)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuestCyan,
                        letterSpacing = 1.sp
                    )
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("tutorial_skip_button")
                    ) {
                        Text(
                            text = "SKIP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                AnimatedContent(targetState = step, label = "tutorial_step") { currentStep ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        when (currentStep) {
                            0 -> TutorialStepSwipe()
                            1 -> TutorialStepHintsAndScore()
                            else -> TutorialStepLevelsAndSave()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Step Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until totalSteps) {
                        Box(
                            modifier = Modifier
                                .size(if (i == step) 10.dp else 7.dp)
                                .clip(CircleShape)
                                .background(if (i == step) QuestGold else SlateNavy)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (step > 0) {
                        OutlinedButton(
                            onClick = { step-- },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("tutorial_back_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("BACK", fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    Button(
                        onClick = {
                            if (step < totalSteps - 1) {
                                step++
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("tutorial_next_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = QuestGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (step < totalSteps - 1) "NEXT" else "LET'S PLAY!",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TutorialStepSwipe() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(QuestGold.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.TouchApp,
            contentDescription = null,
            tint = QuestGold,
            modifier = Modifier.size(32.dp)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = "Swipe in 8 Directions",
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        color = QuestGoldLight,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Drag your finger across adjacent letters horizontally, vertically, or diagonally—forwards or backwards—to highlight hidden words from the list!",
        fontSize = 13.sp,
        color = TextPrimary,
        textAlign = TextAlign.Center,
        lineHeight = 19.sp
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Visual Mini Demo Row: W - O - R - D
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardNavy)
            .border(1.dp, QuestEmerald, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        listOf('W', 'O', 'R', 'D').forEach { letter ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(QuestEmerald.copy(alpha = 0.25f))
                    .border(1.5.dp, QuestEmerald, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TutorialStepHintsAndScore() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(QuestCyan.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = QuestGold,
            modifier = Modifier.size(32.dp)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = "Earn Points & Use Hints",
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        color = QuestGoldLight,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Every discovered word adds points directly to your Total Score! Stuck on a tricky word? Tap the Hint button (💡) in the top bar to illuminate the word's letters on the grid.",
        fontSize = 13.sp,
        color = TextPrimary,
        textAlign = TextAlign.Center,
        lineHeight = 19.sp
    )
}

@Composable
private fun TutorialStepLevelsAndSave() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(QuestEmerald.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = QuestEmerald,
            modifier = Modifier.size(32.dp)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = "20 Quests & Auto-Save",
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        color = QuestGoldLight,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Conquer 20 themed levels from 5×5 Easy grids up to 10×10 Ultimate Challenges! Your progress and scores save automatically on your device, with optional Google Cloud Sync in Settings.",
        fontSize = 13.sp,
        color = TextPrimary,
        textAlign = TextAlign.Center,
        lineHeight = 19.sp
    )
}
