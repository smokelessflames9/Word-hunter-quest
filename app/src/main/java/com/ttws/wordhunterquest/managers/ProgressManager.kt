package com.ttws.wordhunterquest.managers

import android.content.Context
import android.content.SharedPreferences

class ProgressManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "word_hunter_quest_prefs"
        private const val KEY_HIGHEST_UNLOCKED = "highest_unlocked_level"
        private const val KEY_COMPLETED_LEVELS = "completed_levels_set"
        private const val KEY_TOTAL_SCORE = "persistent_total_score"
        private const val KEY_LEVEL_BEST_SCORE_PREFIX = "best_score_level_"
        private const val KEY_HINTS_COUNT = "available_hints_count"
        private const val KEY_SOUND_ENABLED = "setting_sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "setting_vibration_enabled"
        private const val KEY_COMPLETED_LEVELS_COUNT = "completed_count_for_ads"
    }

    fun getHighestUnlockedLevel(): Int {
        return prefs.getInt(KEY_HIGHEST_UNLOCKED, 1).coerceAtLeast(1)
    }

    fun unlockLevel(levelId: Int) {
        val current = getHighestUnlockedLevel()
        if (levelId > current) {
            prefs.edit().putInt(KEY_HIGHEST_UNLOCKED, levelId.coerceAtMost(20)).apply()
        }
    }

    fun isLevelUnlocked(levelId: Int): Boolean {
        if (levelId == 1) return true
        return levelId <= getHighestUnlockedLevel()
    }

    fun isLevelCompleted(levelId: Int): Boolean {
        val set = prefs.getStringSet(KEY_COMPLETED_LEVELS, emptySet()) ?: emptySet()
        return set.contains(levelId.toString())
    }

    fun markLevelCompleted(levelId: Int, score: Int) {
        val set = (prefs.getStringSet(KEY_COMPLETED_LEVELS, emptySet()) ?: emptySet()).toMutableSet()
        set.add(levelId.toString())
        
        val currentBest = getBestScoreForLevel(levelId)
        val newBest = maxOf(currentBest, score)
        
        val currentAdsCount = prefs.getInt(KEY_COMPLETED_LEVELS_COUNT, 0)

        prefs.edit()
            .putStringSet(KEY_COMPLETED_LEVELS, set)
            .putInt(KEY_LEVEL_BEST_SCORE_PREFIX + levelId, newBest)
            .putInt(KEY_COMPLETED_LEVELS_COUNT, currentAdsCount + 1)
            .apply()

        // Unlock next level if available
        if (levelId < 20) {
            unlockLevel(levelId + 1)
        }
    }

    fun getCompletedLevelsCountForAds(): Int {
        return prefs.getInt(KEY_COMPLETED_LEVELS_COUNT, 0)
    }

    fun getBestScoreForLevel(levelId: Int): Int {
        return prefs.getInt(KEY_LEVEL_BEST_SCORE_PREFIX + levelId, 0)
    }

    /**
     * Persistent total score counter that updates as the user correctly solves
     * each word puzzle.
     */
    fun getTotalScore(): Int {
        return prefs.getInt(KEY_TOTAL_SCORE, 0)
    }

    fun addTotalScore(points: Int) {
        val current = getTotalScore()
        val newScore = (current + points).coerceAtLeast(0)
        prefs.edit().putInt(KEY_TOTAL_SCORE, newScore).apply()
    }

    fun getHintsCount(): Int {
        return prefs.getInt(KEY_HINTS_COUNT, 3)
    }

    fun useHint(): Boolean {
        val current = getHintsCount()
        if (current > 0) {
            prefs.edit().putInt(KEY_HINTS_COUNT, current - 1).apply()
            return true
        }
        return false
    }

    fun addHint(amount: Int = 1) {
        val current = getHintsCount()
        prefs.edit().putInt(KEY_HINTS_COUNT, current + amount).apply()
    }

    fun isSoundEnabled(): Boolean {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun isVibrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    }

    fun resetAllProgress() {
        prefs.edit()
            .putInt(KEY_HIGHEST_UNLOCKED, 1)
            .putStringSet(KEY_COMPLETED_LEVELS, emptySet())
            .putInt(KEY_TOTAL_SCORE, 0)
            .putInt(KEY_HINTS_COUNT, 3)
            .putInt(KEY_COMPLETED_LEVELS_COUNT, 0)
            .apply()

        // Clear best scores for all 20 levels
        val editor = prefs.edit()
        for (i in 1..20) {
            editor.remove(KEY_LEVEL_BEST_SCORE_PREFIX + i)
        }
        editor.apply()
    }
}
