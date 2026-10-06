package com.ttws.wordhunterquest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ttws.wordhunterquest.ui.theme.DeepNavy
import com.ttws.wordhunterquest.ui.theme.MidnightBlue
import com.ttws.wordhunterquest.ui.theme.QuestAmber
import com.ttws.wordhunterquest.ui.theme.QuestCyan
import com.ttws.wordhunterquest.ui.theme.QuestGold
import com.ttws.wordhunterquest.ui.theme.QuestGoldLight
import com.ttws.wordhunterquest.ui.theme.SlateNavy

@Composable
fun GameLogo(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val goldGradient = Brush.verticalGradient(
        colors = listOf(QuestGoldLight, QuestGold, QuestAmber)
    )

    Column(
        modifier = modifier.testTag("game_logo"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon badge with puzzle border
        Box(
            modifier = Modifier
                .size(if (compact) 64.dp else 84.dp)
                .shadow(12.dp, RoundedCornerShape(22.dp))
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(DeepNavy, MidnightBlue)
                    )
                )
                .border(2.dp, QuestGold, RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Stylized 'W' badge with Search Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "W",
                    fontSize = if (compact) 36.sp else 48.sp,
                    fontWeight = FontWeight.Black,
                    color = QuestGold
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = QuestCyan,
                    modifier = Modifier
                        .size(if (compact) 24.dp else 30.dp)
                        .padding(start = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(if (compact) 6.dp else 12.dp))

        // Stacked Title: WORD HUNTER QUEST
        Text(
            text = "WORD",
            fontSize = if (compact) 22.sp else 30.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 4.sp,
            color = QuestGoldLight
        )
        Text(
            text = "HUNTER",
            fontSize = if (compact) 26.sp else 36.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 5.sp,
            color = QuestCyan
        )

        // QUEST Ribbon
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.horizontalGradient(listOf(QuestAmber, QuestGold, QuestAmber)))
                .padding(horizontal = 16.dp, vertical = 3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "QUEST",
                    fontSize = if (compact) 12.sp else 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
