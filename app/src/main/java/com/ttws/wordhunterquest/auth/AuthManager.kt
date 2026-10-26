package com.ttws.wordhunterquest.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.ttws.wordhunterquest.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Safely unwraps ContextWrapper instances to retrieve the hosting Activity.
 */
fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

class AuthManager(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager: CredentialManager = CredentialManager.create(context)
    private val tag = "Auth"

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    /**
     * Silent background auto-sign-in using GetGoogleIdOption with an Activity context.
     */
    fun attemptAutoSignIn(
        activity: Activity,
        scope: CoroutineScope,
        onSuccess: () -> Unit = {},
        onFailed: () -> Unit = {}
    ) {
        if (auth.currentUser != null) {
            onSuccess()
            return
        }

        val clientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            Log.w(tag, "default_web_client_id not available for auto sign-in: ${e.message}")
            onFailed()
            return
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    auth.signInWithCredential(authCredential).await()
                    Log.d(tag, "Silent auto-sign-in succeeded for: ${auth.currentUser?.email}")
                    onSuccess()
                } else {
                    onFailed()
                }
            } catch (e: Exception) {
                Log.d(tag, "Silent auto-sign-in skipped: ${e.message}")
                onFailed()
            }
        }
    }

    /**
     * Interactive Google Sign-In using GetSignInWithGoogleOption.
     */
    fun signInWithGoogle(
        activity: Activity,
        scope: CoroutineScope,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onCancelled: () -> Unit
    ) {
        val clientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            val err = "Google Sign-In configuration missing: default_web_client_id not found"
            Log.e(tag, err)
            onError(err)
            return
        }

        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    auth.signInWithCredential(authCredential).await()
                    Log.d(tag, "Interactive Google Sign-In succeeded: ${auth.currentUser?.email}")
                    onSuccess()
                } else {
                    onError("Unexpected credential format received.")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w(tag, "Google Sign-In cancelled or dismissed: ${e.message}", e)
                onCancelled()
            } catch (e: NoCredentialException) {
                Log.w(tag, "No Google account available on device: ${e.message}", e)
                onError("No Google account found on this device. Please add a Google account in Android Settings or continue with Local Auto-Save.")
            } catch (e: Exception) {
                Log.e(tag, "Google Sign-In failed", e)
                onError(e.localizedMessage ?: "Sign-in failed. Please try again.")
            }
        }
    }

    /**
     * Sign out of Firebase and clear Credential Manager state.
     */
    fun signOut(
        scope: CoroutineScope,
        onComplete: () -> Unit = {}
    ) {
        auth.signOut()
        scope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
                Log.d(tag, "Credential state cleared on sign out")
            } catch (e: Exception) {
                Log.e(tag, "Failed to clear credential state: ${e.message}", e)
            } finally {
                onComplete()
            }
        }
    }

    /**
     * Delete user account from Firebase Auth and clear credentials.
     */
    suspend fun deleteAccount() {
        auth.currentUser?.delete()?.await()
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (_: Exception) {}
    }
}
