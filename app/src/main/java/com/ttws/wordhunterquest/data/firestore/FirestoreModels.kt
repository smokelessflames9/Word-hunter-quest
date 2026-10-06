package com.ttws.wordhunterquest.data.firestore

import com.google.firebase.Timestamp

data class UserProfile(
    val userId: String = "",
    val displayName: String = "",
    val highestUnlockedLevel: Int = 1,
    val totalScore: Int = 0,
    val hintsRemaining: Int = 3,
    val completedLevels: List<Int> = emptyList(),
    val updatedAt: Timestamp? = null
)

data class LevelProgress(
    val userId: String = "",
    val levelId: Int = 1,
    val completed: Boolean = false,
    val bestScore: Int = 0,
    val updatedAt: Timestamp? = null
)
