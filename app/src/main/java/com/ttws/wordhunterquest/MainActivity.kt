package com.ttws.wordhunterquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.ttws.wordhunterquest.ads.AdMobManager
import com.ttws.wordhunterquest.ui.screens.AboutScreen
import com.ttws.wordhunterquest.ui.screens.AuthScreen
import com.ttws.wordhunterquest.ui.screens.GameScreen
import com.ttws.wordhunterquest.ui.screens.HomeScreen
import com.ttws.wordhunterquest.ui.screens.LevelSelectScreen
import com.ttws.wordhunterquest.ui.screens.SettingsScreen
import com.ttws.wordhunterquest.ui.theme.MidnightBlue
import com.ttws.wordhunterquest.ui.theme.WordHunterQuestTheme
import com.ttws.wordhunterquest.ui.viewmodel.GameViewModel
import com.ttws.wordhunterquest.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Safely initialize AdMob SDK on startup (fails gracefully if offline)
        AdMobManager.initialize(this)

        setContent {
            WordHunterQuestTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    color = MidnightBlue
                ) {
                    val uiState by viewModel.uiState.collectAsState()

                    when (uiState.currentScreen) {
                        Screen.AUTH -> {
                            AuthScreen(
                                authManager = viewModel.authManager,
                                onAuthSuccess = { viewModel.navigateTo(Screen.HOME) },
                                onContinueOffline = { viewModel.navigateTo(Screen.HOME) }
                            )
                        }
                        Screen.HOME -> {
                            HomeScreen(
                                uiState = uiState,
                                onNavigate = { viewModel.navigateTo(it) },
                                onPlay = {
                                    // Start highest unlocked level
                                    viewModel.startLevel(uiState.highestUnlockedLevel)
                                }
                            )
                        }
                        Screen.LEVEL_SELECT -> {
                            LevelSelectScreen(
                                uiState = uiState,
                                onLevelSelected = { levelId ->
                                    viewModel.startLevel(levelId)
                                },
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )
                        }
                        Screen.GAME -> {
                            GameScreen(
                                uiState = uiState,
                                onWordSelected = { word, cells ->
                                    viewModel.onWordSelected(word, cells)
                                },
                                onHintRequested = { activity ->
                                    viewModel.onHintRequested(activity)
                                },
                                onRestart = { viewModel.restartCurrentLevel() },
                                onTogglePause = { viewModel.togglePause() },
                                onNextLevel = { activity ->
                                    viewModel.nextLevel(activity)
                                },
                                onBackToLevels = { viewModel.navigateTo(Screen.LEVEL_SELECT) }
                            )
                        }
                        Screen.SETTINGS -> {
                            SettingsScreen(
                                uiState = uiState,
                                onToggleSound = { viewModel.toggleSound() },
                                onToggleVibration = { viewModel.toggleVibration() },
                                onShowResetConfirmation = { viewModel.showResetConfirmation(it) },
                                onConfirmReset = { viewModel.resetAllProgress() },
                                onSignOut = { viewModel.signOut() },
                                onSignInRequested = { viewModel.navigateTo(Screen.AUTH) },
                                onNavigate = { viewModel.navigateTo(it) },
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )
                        }
                        Screen.ABOUT -> {
                            AboutScreen(
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )
                        }
                    }
                }
            }
        }
    }
}
