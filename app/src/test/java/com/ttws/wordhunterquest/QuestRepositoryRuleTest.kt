package com.ttws.wordhunterquest

import com.ttws.wordhunterquest.data.firestore.QuestRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun testAuthenticatedUserCanSaveAndObserveProgress() = runBlocking {
        val uid = signInTestUser("explorer1@example.com")
        val repository = QuestRepository(firestore)

        repository.saveUserProfile(
            highestUnlockedLevel = 3,
            totalScore = 450,
            hintsRemaining = 2,
            completedLevels = listOf(1, 2)
        )

        repository.saveLevelProgress(
            levelId = 1,
            completed = true,
            bestScore = 200
        )

        val profile = repository.observeUserProfile().first()
        assertNotNull("Observed profile should not be null", profile)
        assertEquals(uid, profile?.userId)
        assertEquals(3, profile?.highestUnlockedLevel)
        assertEquals(450, profile?.totalScore)
        assertEquals(2, profile?.hintsRemaining)
        assertEquals(listOf(1, 2), profile?.completedLevels)
    }

    @Test(expected = IllegalStateException::class)
    fun testUnauthenticatedUserCannotSaveProfile() = runBlocking {
        auth.signOut()
        val repository = QuestRepository(firestore)

        repository.saveUserProfile(
            highestUnlockedLevel = 1,
            totalScore = 100,
            hintsRemaining = 3,
            completedLevels = listOf(1)
        )
    }
}
