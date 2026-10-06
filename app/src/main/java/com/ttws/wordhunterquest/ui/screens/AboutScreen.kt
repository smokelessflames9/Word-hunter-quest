package com.ttws.wordhunterquest.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttws.wordhunterquest.ui.components.BannerAdView
import com.ttws.wordhunterquest.ui.components.GameLogo
import com.ttws.wordhunterquest.ui.theme.CardNavy
import com.ttws.wordhunterquest.ui.theme.MidnightBlue
import com.ttws.wordhunterquest.ui.theme.QuestCyan
import com.ttws.wordhunterquest.ui.theme.QuestGold
import com.ttws.wordhunterquest.ui.theme.QuestGoldLight
import com.ttws.wordhunterquest.ui.theme.SlateNavy
import com.ttws.wordhunterquest.ui.theme.TextPrimary
import com.ttws.wordhunterquest.ui.theme.TextSecondary

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlue)
            .testTag("about_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
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
                modifier = Modifier.testTag("about_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ABOUT",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = QuestGoldLight
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game Logo (matching home screen and launcher icon)
            GameLogo(
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Game details card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "WORD HUNTER QUEST",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = QuestGoldLight,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "A thrilling word search adventure packed with 20 progressively challenging levels across distinct themes. Hunt for words hidden horizontally, vertically, diagonally, forward and backward!",
                        fontSize = 13.sp,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "DEVELOPED BY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Text(
                                text = "Thomas Tech WS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = QuestCyan
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "VERSION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Text(
                                text = "1.0.0",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = QuestGold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Features Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "FEATURES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuestCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureBullet("20 unique themed levels with guaranteed solvability")
                    FeatureBullet("Real-time persistent score counter across all levels")
                    FeatureBullet("Intuitive 8-direction smooth touch-and-drag word selection")
                    FeatureBullet("Limited hint system with rewarded video option")
                    FeatureBullet("Offline capable gameplay with local progress saving")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Banner Ad
        BannerAdView(modifier = Modifier.padding(bottom = 6.dp))
    }
}

@Composable
private fun FeatureBullet(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = QuestGold,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = TextSecondary
        )
    }
}
