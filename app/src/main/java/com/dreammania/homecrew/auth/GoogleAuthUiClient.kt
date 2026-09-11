package com.dreammania.homecrew.auth

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.dreammania.homecrew.R
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(private val context: Context) {

    private val auth = Firebase.auth

    suspend fun signInWithCredential(credential: Credential): SignInResult {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            return try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
                val user = auth.signInWithCredential(googleCredentials).await().user
                SignInResult(
                    data = user?.let {
                        UserData(
                            userId = it.uid,
                            username = it.displayName,
                            profilePictureUrl = it.photoUrl?.toString()
                        )
                    },
                    errorMessage = null
                )
            } catch (e: GoogleIdTokenParsingException) {
                SignInResult(data = null, errorMessage = "Google ID token parsing failed: ${e.message}")
            } catch (e: Exception) {
                SignInResult(data = null, errorMessage = e.message)
            }
        }
        return SignInResult(data = null, errorMessage = "Sign-in credential is not a Google ID token.")
    }

    suspend fun signOut() {
        auth.signOut()
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.let {
        UserData(
            userId = it.uid,
            username = it.displayName,
            profilePictureUrl = it.photoUrl?.toString()
        )
    }

    companion object {
        fun create(context: Context): GoogleAuthUiClient {
            return GoogleAuthUiClient(context)
        }
    }
}

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)
