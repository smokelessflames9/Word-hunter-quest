package com.ttws.wordhunterquest.managers

class ScoreManager(private val progressManager: ProgressManager) {

    private var currentLevelScore: Int = 0
    private var lastFoundTimestamp: Long = System.currentTimeMillis()

    fun resetLevelScore() {
        currentLevelScore = 0
        lastFoundTimestamp = System.currentTimeMillis()
    }

    fun getCurrentLevelScore(): Int = currentLevelScore

    fun getPersistentTotalScore(): Int = progressManager.getTotalScore()

    /**
     * Calculates points for finding a word, updates level score and persistent score counter immediately.
     */
    fun onWordFound(word: String): Int {
        val now = System.currentTimeMillis()
        val elapsedSeconds = ((now - lastFoundTimestamp) / 1000).toInt()
        lastFoundTimestamp = now

        // Base points + length bonus
        val basePoints = 100
        val lengthBonus = word.length * 25

        // Speed bonus: up to 100 bonus points if found within 15 seconds
        val speedBonus = if (elapsedSeconds < 15) {
            (15 - elapsedSeconds) * 7
        } else {
            0
        }

        val totalAwarded = basePoints + lengthBonus + speedBonus
        currentLevelScore += totalAwarded

        // Update persistent score counter immediately
        progressManager.addTotalScore(totalAwarded)

        return totalAwarded
    }

    fun onIncorrectSelection(): Int {
        val penalty = 20
        if (currentLevelScore > 0) {
            currentLevelScore = maxOf(0, currentLevelScore - penalty)
        }
        return -penalty
    }

    fun onHintUsed(): Int {
        val penalty = 30
        if (currentLevelScore > 0) {
            currentLevelScore = maxOf(0, currentLevelScore - penalty)
        }
        return -penalty
    }

    fun onLevelCompleted(levelId: Int): Int {
        val levelCompletionBonus = 500
        currentLevelScore += levelCompletionBonus
        progressManager.addTotalScore(levelCompletionBonus)
        progressManager.markLevelCompleted(levelId, currentLevelScore)
        return currentLevelScore
    }
}
