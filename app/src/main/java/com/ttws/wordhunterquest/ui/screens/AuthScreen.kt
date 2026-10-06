package com.ttws.wordhunterquest.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttws.wordhunterquest.auth.AuthManager
import com.ttws.wordhunterquest.ui.components.BannerAdView
import com.ttws.wordhunterquest.ui.components.GameLogo
import com.ttws.wordhunterquest.ui.theme.CardNavy
import com.ttws.wordhunterquest.ui.theme.MidnightBlue
import com.ttws.wordhunterquest.ui.theme.QuestCyan
import com.ttws.wordhunterquest.ui.theme.QuestGold
import com.ttws.wordhunterquest.ui.theme.QuestGoldLight
import com.ttws.wordhunterquest.ui.theme.QuestRuby
import com.ttws.wordhunterquest.ui.theme.SlateNavy
import com.ttws.wordhunterquest.ui.theme.TextPrimary
import com.ttws.wordhunterquest.ui.theme.TextSecondary

@Composable
fun AuthScreen(
    authManager: AuthManager,
    onAuthSuccess: () -> Unit,
    onContinueOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .testTag("auth_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 400.dp)
        ) {
            // Game Logo
            GameLogo(modifier = Modifier.padding(bottom = 24.dp))

            // Cloud Save Perks Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "CLOUD SAVE & PROGRESSION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuestCyan,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BenefitRow(
                        icon = Icons.Default.CloudDone,
                        text = "Backup your 20-level quest progress to Firestore"
                    )
                    BenefitRow(
                        icon = Icons.Default.EmojiEvents,
                        text = "Save and track your total score across devices"
                    )
                    BenefitRow(
                        icon = Icons.Default.Security,
                        text = "Secure Google Sign-In with official Firebase Auth"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error display if any
            errorMessage?.let { error ->
                Text(
                    text = error,
                    fontSize = 12.sp,
                    color = QuestRuby,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Google Sign-In Button
            Button(
                onClick = {
                    if (activity != null) {
                        isLoading = true
                        errorMessage = null
                        authManager.signInWithGoogle(
                            activity = activity,
                            scope = scope,
                            onSuccess = {
                                isLoading = false
                                onAuthSuccess()
                            },
                            onError = { err ->
                                isLoading = false
                                errorMessage = err
                            },
                            onCancelled = {
                                isLoading = false
                            }
                        )
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .shadow(8.dp, RoundedCornerShape(14.dp))
                    .testTag("sign_in_with_google_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = QuestGold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sign in with Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Optional Offline Play Button
            TextButton(
                onClick = onContinueOffline,
                modifier = Modifier.testTag("continue_offline_button")
            ) {
                Text(
                    text = "Continue as Guest (Local Offline)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
        }

        // Real Banner Ad at bottom
        BannerAdView(modifier = Modifier.padding(bottom = 4.dp))
    }
}

@Composable
private fun BenefitRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = QuestGold,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = TextPrimary
        )
    }
}
