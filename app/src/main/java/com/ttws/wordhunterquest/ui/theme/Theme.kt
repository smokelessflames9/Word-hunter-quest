package com.ttws.wordhunterquest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val QuestColorScheme = darkColorScheme(
    primary = QuestGold,
    onPrimary = Color.Black,
    primaryContainer = QuestAmber,
    onPrimaryContainer = Color.White,
    secondary = QuestCyan,
    onSecondary = Color.Black,
    secondaryContainer = SlateNavy,
    onSecondaryContainer = QuestCyanLight,
    tertiary = QuestEmerald,
    onTertiary = Color.Black,
    background = MidnightBlue,
    onBackground = TextPrimary,
    surface = DeepNavy,
    onSurface = TextPrimary,
    surfaceVariant = SlateNavy,
    onSurfaceVariant = TextSecondary,
    error = QuestRuby,
    onError = Color.White
)

@Composable
fun WordHunterQuestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = QuestColorScheme,
        typography = Typography,
        content = content
    )
}
