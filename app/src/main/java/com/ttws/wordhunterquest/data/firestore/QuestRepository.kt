package com.ttws.wordhunterquest.data.firestore

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.ttws.wordhunterquest.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class QuestRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    /**
     * Real-time observation of user progress profile using snapshots() Flow.
     */
    fun observeUserProfile(): Flow<UserProfile?> = flow {
        val uid = requireUserId()
        val docPath = "users/$uid"
        val docRef = db.collection("users").document(uid)

        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(UserProfile::class.java)
                    } else {
                        null
                    }
                }
                .catch { error ->
                    if (error is Exception) {
                        handleFirestoreError(error, OperationType.GET, docPath)
                    }
                    throw error
                }
        )
    }

    suspend fun saveUserProfile(
        highestUnlockedLevel: Int,
        totalScore: Int,
        hintsRemaining: Int,
        completedLevels: List<Int>
    ) {
        val uid = requireUserId()
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)

        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "displayName" to (auth.currentUser?.displayName ?: "Explorer"),
            "highestUnlockedLevel" to highestUnlockedLevel.coerceIn(1, 20),
            "totalScore" to totalScore.coerceAtLeast(0),
            "hintsRemaining" to hintsRemaining.coerceAtLeast(0),
            "completedLevels" to completedLevels,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    suspend fun saveLevelProgress(
        levelId: Int,
        completed: Boolean,
        bestScore: Int
    ) {
        val uid = requireUserId()
        val path = "users/$uid/levels/$levelId"
        val docRef = db.collection("users").document(uid).collection("levels").document(levelId.toString())

        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "levelId" to levelId,
            "completed" to completed,
            "bestScore" to bestScore,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    suspend fun syncLocalWithCloud(
        localTotalScore: Int,
        localHighestLevel: Int,
        localHints: Int,
        localCompleted: Set<Int>
    ): UserProfile {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val snapshot = docRef.get().await()

        if (snapshot.exists()) {
            val cloudProfile = snapshot.toObject(UserProfile::class.java)
            if (cloudProfile != null) {
                // Merge cloud and local data: take max scores and union completed levels
                val mergedHighest = maxOf(cloudProfile.highestUnlockedLevel, localHighestLevel)
                val mergedTotalScore = maxOf(cloudProfile.totalScore, localTotalScore)
                val mergedHints = maxOf(cloudProfile.hintsRemaining, localHints)
                val mergedCompleted = (cloudProfile.completedLevels + localCompleted).distinct().sorted()

                saveUserProfile(
                    highestUnlockedLevel = mergedHighest,
                    totalScore = mergedTotalScore,
                    hintsRemaining = mergedHints,
                    completedLevels = mergedCompleted
                )

                return UserProfile(
                    userId = uid,
                    displayName = cloudProfile.displayName,
                    highestUnlockedLevel = mergedHighest,
                    totalScore = mergedTotalScore,
                    hintsRemaining = mergedHints,
                    completedLevels = mergedCompleted
                )
            }
        }

        // Initialize cloud with current local progress
        val newProfile = UserProfile(
            userId = uid,
            displayName = auth.currentUser?.displayName ?: "Explorer",
            highestUnlockedLevel = localHighestLevel,
            totalScore = localTotalScore,
            hintsRemaining = localHints,
            completedLevels = localCompleted.toList().sorted()
        )

        saveUserProfile(
            highestUnlockedLevel = newProfile.highestUnlockedLevel,
            totalScore = newProfile.totalScore,
            hintsRemaining = newProfile.hintsRemaining,
            completedLevels = newProfile.completedLevels
        )

        return newProfile
    }
}
