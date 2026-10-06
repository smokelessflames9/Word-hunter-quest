package com.ttws.wordhunterquest.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ttws.wordhunterquest.ads.InterstitialAdManager
import com.ttws.wordhunterquest.ads.RewardedAdManager
import com.ttws.wordhunterquest.auth.AuthManager
import com.ttws.wordhunterquest.data.CellPosition
import com.ttws.wordhunterquest.data.Level
import com.ttws.wordhunterquest.data.LevelRepository
import com.ttws.wordhunterquest.data.firestore.QuestRepository
import com.ttws.wordhunterquest.managers.ProgressManager
import com.ttws.wordhunterquest.managers.ScoreManager
import com.ttws.wordhunterquest.managers.SoundManager
import com.ttws.wordhunterquest.managers.VibrationManager
import com.ttws.wordhunterquest.ui.components.DiscoveredWordDisplay
import com.ttws.wordhunterquest.ui.theme.WordHighlightPalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

enum class Screen {
    AUTH,
    HOME,
    LEVEL_SELECT,
    GAME,
    SETTINGS,
    ABOUT
}

data class GameUiState(
    val currentScreen: Screen = Screen.AUTH,
    val currentLevel: Level? = null,
    val discoveredWords: List<DiscoveredWordDisplay> = emptyList(),
    val remainingWords: List<String> = emptyList(),
    val levelScore: Int = 0,
    val persistentTotalScore: Int = 0,
    val hintsRemaining: Int = 3,
    val highestUnlockedLevel: Int = 1,
    val completedLevels: Set<Int> = emptySet(),
    val isLevelCompleted: Boolean = false,
    val isPaused: Boolean = false,
    val isIncorrectFlash: Boolean = false,
    val hintCells: List<CellPosition> = emptyList(),
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val toastMessage: String? = null,
    val showResetConfirmationDialog: Boolean = false,
    val isSignedIn: Boolean = false,
    val userDisplayName: String? = null,
    val userEmail: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val progressManager = ProgressManager(application)
    val scoreManager = ScoreManager(progressManager)
    val soundManager = SoundManager(progressManager)
    val vibrationManager = VibrationManager(application, progressManager)
    val interstitialAdManager = InterstitialAdManager(application)
    val rewardedAdManager = RewardedAdManager(application)
    val authManager = AuthManager(application)
    val questRepository = QuestRepository(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadInitialState()
        observeAuthState()
        authManager.attemptAutoSignIn(viewModelScope)
    }

    private fun loadInitialState() {
        val completed = (1..20).filter { progressManager.isLevelCompleted(it) }.toSet()
        _uiState.value = _uiState.value.copy(
            persistentTotalScore = progressManager.getTotalScore(),
            hintsRemaining = progressManager.getHintsCount(),
            highestUnlockedLevel = progressManager.getHighestUnlockedLevel(),
            completedLevels = completed,
            soundEnabled = progressManager.isSoundEnabled(),
            vibrationEnabled = progressManager.isVibrationEnabled()
        )
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authManager.currentUser.collect { user ->
                val signedIn = user != null
                val targetScreen = if (signedIn && _uiState.value.currentScreen == Screen.AUTH) {
                    Screen.HOME
                } else {
                    _uiState.value.currentScreen
                }

                _uiState.value = _uiState.value.copy(
                    isSignedIn = signedIn,
                    userDisplayName = user?.displayName,
                    userEmail = user?.email,
                    currentScreen = targetScreen
                )
                if (signedIn) {
                    syncWithFirestore()
                }
            }
        }
    }

    private fun syncWithFirestore() {
        viewModelScope.launch {
            try {
                val synced = questRepository.syncLocalWithCloud(
                    localTotalScore = progressManager.getTotalScore(),
                    localHighestLevel = progressManager.getHighestUnlockedLevel(),
                    localHints = progressManager.getHintsCount(),
                    localCompleted = _uiState.value.completedLevels
                )

                // Sync down to local progress
                progressManager.unlockLevel(synced.highestUnlockedLevel)
                synced.completedLevels.forEach { lvl ->
                    progressManager.markLevelCompleted(lvl, progressManager.getBestScoreForLevel(lvl))
                }

                _uiState.value = _uiState.value.copy(
                    highestUnlockedLevel = synced.highestUnlockedLevel,
                    persistentTotalScore = synced.totalScore,
                    hintsRemaining = synced.hintsRemaining,
                    completedLevels = synced.completedLevels.toSet()
                )

                // Attach real-time cloud observation
                launch {
                    questRepository.observeUserProfile()
                        .catch { e -> Log.w("GameViewModel", "Firestore observe error: ${e.message}") }
                        .collect { profile ->
                            if (profile != null) {
                                _uiState.value = _uiState.value.copy(
                                    highestUnlockedLevel = profile.highestUnlockedLevel,
                                    persistentTotalScore = profile.totalScore,
                                    hintsRemaining = profile.hintsRemaining,
                                    completedLevels = profile.completedLevels.toSet()
                                )
                            }
                        }
                }
            } catch (e: Exception) {
                Log.w("GameViewModel", "Cloud sync exception: ${e.message}")
            }
        }
    }

    fun signOut() {
        authManager.signOut(viewModelScope) {
            _uiState.value = _uiState.value.copy(
                isSignedIn = false,
                userDisplayName = null,
                userEmail = null
            )
            showToast("Signed out of Google account.")
        }
    }

    fun navigateTo(screen: Screen) {
        soundManager.playSound(SoundManager.SoundType.BUTTON_CLICK)
        vibrationManager.vibrate(VibrationManager.VibeType.LIGHT_TICK)
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun startLevel(levelId: Int) {
        val level = LevelRepository.getLevel(levelId) ?: return
        scoreManager.resetLevelScore()

        _uiState.value = _uiState.value.copy(
            currentScreen = Screen.GAME,
            currentLevel = level,
            discoveredWords = emptyList(),
            remainingWords = level.words,
            levelScore = 0,
            persistentTotalScore = progressManager.getTotalScore(),
            isLevelCompleted = false,
            isPaused = false,
            hintCells = emptyList(),
            isIncorrectFlash = false
        )
        soundManager.playSound(SoundManager.SoundType.BUTTON_CLICK)
    }

    fun restartCurrentLevel() {
        val level = _uiState.value.currentLevel ?: return
        startLevel(level.id)
    }

    fun nextLevel(activity: Activity) {
        val current = _uiState.value.currentLevel ?: return
        val nextId = current.id + 1
        if (nextId <= LevelRepository.getTotalLevels()) {
            val completedCount = progressManager.getCompletedLevelsCountForAds()
            interstitialAdManager.showInterstitialIfEligible(activity, completedCount) {
                startLevel(nextId)
            }
        } else {
            navigateTo(Screen.LEVEL_SELECT)
        }
    }

    fun togglePause() {
        soundManager.playSound(SoundManager.SoundType.BUTTON_CLICK)
        _uiState.value = _uiState.value.copy(isPaused = !_uiState.value.isPaused)
    }

    fun onWordSelected(selectedLetters: String, selectedCells: List<CellPosition>) {
        val level = _uiState.value.currentLevel ?: return
        if (_uiState.value.isLevelCompleted || _uiState.value.isPaused) return

        val candidate = selectedLetters.uppercase()
        val candidateReversed = candidate.reversed()

        val matchingWord = _uiState.value.remainingWords.firstOrNull {
            it == candidate || it == candidateReversed
        }

        if (matchingWord != null) {
            val updatedRemaining = _uiState.value.remainingWords.filter { it != matchingWord }
            val colorIndex = _uiState.value.discoveredWords.size % WordHighlightPalette.size
            val color = WordHighlightPalette[colorIndex]

            val newDiscoveredWord = DiscoveredWordDisplay(
                word = matchingWord,
                cells = selectedCells,
                color = color
            )
            val updatedDiscovered = _uiState.value.discoveredWords + newDiscoveredWord

            scoreManager.onWordFound(matchingWord)
            val newLevelScore = scoreManager.getCurrentLevelScore()
            val newTotalScore = progressManager.getTotalScore()

            soundManager.playSound(SoundManager.SoundType.CORRECT_WORD)
            vibrationManager.vibrate(VibrationManager.VibeType.WORD_FOUND)

            val isComplete = updatedRemaining.isEmpty()

            _uiState.value = _uiState.value.copy(
                discoveredWords = updatedDiscovered,
                remainingWords = updatedRemaining,
                levelScore = newLevelScore,
                persistentTotalScore = newTotalScore,
                hintCells = emptyList()
            )

            // Save to Firestore if authenticated
            if (_uiState.value.isSignedIn) {
                viewModelScope.launch {
                    try {
                        questRepository.saveUserProfile(
                            highestUnlockedLevel = _uiState.value.highestUnlockedLevel,
                            totalScore = newTotalScore,
                            hintsRemaining = _uiState.value.hintsRemaining,
                            completedLevels = _uiState.value.completedLevels.toList()
                        )
                    } catch (_: Exception) {}
                }
            }

            if (isComplete) {
                onLevelCompleted(level.id)
            }
        } else {
            scoreManager.onIncorrectSelection()
            soundManager.playSound(SoundManager.SoundType.INCORRECT_SELECTION)
            vibrationManager.vibrate(VibrationManager.VibeType.INCORRECT)

            _uiState.value = _uiState.value.copy(
                isIncorrectFlash = true,
                levelScore = scoreManager.getCurrentLevelScore()
            )

            viewModelScope.launch {
                delay(300)
                _uiState.value = _uiState.value.copy(isIncorrectFlash = false)
            }
        }
    }

    private fun onLevelCompleted(levelId: Int) {
        val finalLevelScore = scoreManager.onLevelCompleted(levelId)
        soundManager.playSound(SoundManager.SoundType.LEVEL_COMPLETED)
        vibrationManager.vibrate(VibrationManager.VibeType.LEVEL_COMPLETE)

        val updatedCompleted = _uiState.value.completedLevels + levelId
        val updatedUnlocked = progressManager.getHighestUnlockedLevel()

        _uiState.value = _uiState.value.copy(
            isLevelCompleted = true,
            levelScore = finalLevelScore,
            persistentTotalScore = progressManager.getTotalScore(),
            completedLevels = updatedCompleted,
            highestUnlockedLevel = updatedUnlocked
        )

        // Save progress to Cloud Firestore
        if (_uiState.value.isSignedIn) {
            viewModelScope.launch {
                try {
                    questRepository.saveUserProfile(
                        highestUnlockedLevel = updatedUnlocked,
                        totalScore = progressManager.getTotalScore(),
                        hintsRemaining = _uiState.value.hintsRemaining,
                        completedLevels = updatedCompleted.toList()
                    )
                    questRepository.saveLevelProgress(
                        levelId = levelId,
                        completed = true,
                        bestScore = finalLevelScore
                    )
                } catch (_: Exception) {}
            }
        }
    }

    fun onHintRequested(activity: Activity) {
        val level = _uiState.value.currentLevel ?: return
        if (_uiState.value.remainingWords.isEmpty()) return

        val hints = progressManager.getHintsCount()

        if (hints > 0) {
            progressManager.useHint()
            revealNextWord(level)
            val remaining = progressManager.getHintsCount()
            _uiState.value = _uiState.value.copy(hintsRemaining = remaining)

            if (_uiState.value.isSignedIn) {
                viewModelScope.launch {
                    try {
                        questRepository.saveUserProfile(
                            highestUnlockedLevel = _uiState.value.highestUnlockedLevel,
                            totalScore = _uiState.value.persistentTotalScore,
                            hintsRemaining = remaining,
                            completedLevels = _uiState.value.completedLevels.toList()
                        )
                    } catch (_: Exception) {}
                }
            }
        } else {
            rewardedAdManager.showRewardedAd(
                activity = activity,
                onRewardEarned = {
                    progressManager.addHint(1)
                    revealNextWord(level)
                    val remaining = progressManager.getHintsCount()
                    _uiState.value = _uiState.value.copy(hintsRemaining = remaining)

                    if (_uiState.value.isSignedIn) {
                        viewModelScope.launch {
                            try {
                                questRepository.saveUserProfile(
                                    highestUnlockedLevel = _uiState.value.highestUnlockedLevel,
                                    totalScore = _uiState.value.persistentTotalScore,
                                    hintsRemaining = remaining,
                                    completedLevels = _uiState.value.completedLevels.toList()
                                )
                            } catch (_: Exception) {}
                        }
                    }
                },
                onUnavailable = {
                    showToast("Reward unavailable. Please try again later.")
                }
            )
        }
    }

    private fun revealNextWord(level: Level) {
        val targetWord = _uiState.value.remainingWords.firstOrNull() ?: return
        val placement = level.placements[targetWord]

        soundManager.playSound(SoundManager.SoundType.HINT)
        vibrationManager.vibrate(VibrationManager.VibeType.HINT)
        scoreManager.onHintUsed()

        if (placement != null) {
            _uiState.value = _uiState.value.copy(
                hintCells = placement.cells,
                levelScore = scoreManager.getCurrentLevelScore()
            )
        }
    }

    fun toggleSound() {
        val newSetting = !progressManager.isSoundEnabled()
        progressManager.setSoundEnabled(newSetting)
        _uiState.value = _uiState.value.copy(soundEnabled = newSetting)
        soundManager.playSound(SoundManager.SoundType.BUTTON_CLICK)
    }

    fun toggleVibration() {
        val newSetting = !progressManager.isVibrationEnabled()
        progressManager.setVibrationEnabled(newSetting)
        _uiState.value = _uiState.value.copy(vibrationEnabled = newSetting)
        vibrationManager.vibrate(VibrationManager.VibeType.LIGHT_TICK)
    }

    fun showResetConfirmation(show: Boolean) {
        soundManager.playSound(SoundManager.SoundType.BUTTON_CLICK)
        _uiState.value = _uiState.value.copy(showResetConfirmationDialog = show)
    }

    fun resetAllProgress() {
        progressManager.resetAllProgress()
        scoreManager.resetLevelScore()
        _uiState.value = _uiState.value.copy(
            highestUnlockedLevel = 1,
            completedLevels = emptySet(),
            persistentTotalScore = 0,
            hintsRemaining = 3,
            showResetConfirmationDialog = false
        )
        if (_uiState.value.isSignedIn) {
            viewModelScope.launch {
                try {
                    questRepository.saveUserProfile(
                        highestUnlockedLevel = 1,
                        totalScore = 0,
                        hintsRemaining = 3,
                        completedLevels = emptyList()
                    )
                } catch (_: Exception) {}
            }
        }
        soundManager.playSound(SoundManager.SoundType.LEVEL_UNLOCKED)
        showToast("Progress has been reset.")
    }

    fun showToast(message: String) {
        _uiState.value = _uiState.value.copy(toastMessage = message)
        viewModelScope.launch {
            delay(2500)
            if (_uiState.value.toastMessage == message) {
                _uiState.value = _uiState.value.copy(toastMessage = null)
            }
        }
    }
}
