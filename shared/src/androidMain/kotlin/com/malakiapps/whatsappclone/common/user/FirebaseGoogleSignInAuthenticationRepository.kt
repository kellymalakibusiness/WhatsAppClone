package com.malakiapps.whatsappclone.common.user

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.malakiapps.whatsappclone.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class FirebaseGoogleSignInAuthenticationRepository : UserAuthenticationRepository {
    private var credentialManager: CredentialManager? = null
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var context: Context? = null

    override fun initializeCredentialManager(context: Context) {
        credentialManager = CredentialManager.create(context)
        this.context = context
    }

    override suspend fun signIn(): User? {
        val result = try {
            buildCredentialRequest()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) {
                throw e
            }
            return null
        }

        return result.handleSignIn()
    }

    override suspend fun anonymousSignIn(): User? {
        return suspendCancellableCoroutine { cont ->
            Firebase.auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    cont.handleOnCompleteSignIn(task)
                }
                .addOnFailureListener {
                    cont.returnNull()
                }
        }
    }

    override suspend fun signOut(): Boolean {
        credentialManager?.clearCredentialState(
            ClearCredentialStateRequest()
        )
        firebaseAuth.signOut()
        return true
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun updateProfile(name: String?): Boolean {
        return suspendCancellableCoroutine { cont ->
            if (name != null) {
                Firebase.auth.currentUser?.let { existingUser ->
                    existingUser.updateProfile(
                        userProfileChangeRequest {
                            displayName = name
                        }
                    )
                        .addOnCompleteListener { task ->
                            cont.resume(true, null)
                        }
                        .addOnFailureListener {
                            cont.resume(false, null)
                        }
                } ?: cont.resume(false, null)

            }
        }
    }

    override fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.let {
            User(
                id = it.uid,
                name = it.displayName ?: "",
                email = it.email ?: "",
                imageUri = it.photoUrl
            )
        }
    }

    private suspend fun GetCredentialResponse.handleSignIn(): User? {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                println("We got ${tokenCredential.id}, ${tokenCredential.displayName}, ${tokenCredential.profilePictureUri}")

                val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                return authResult.user?.let { currentUser ->
                    User(
                        id = currentUser.uid,
                        name = currentUser.displayName ?: "",
                        email = currentUser.email ?: "",
                        imageUri = currentUser.photoUrl
                    )
                }
            } catch (e: GoogleIdTokenParsingException) {
                return null
            }
        } else {
            return null
        }
    }

    private suspend fun buildCredentialRequest(): GetCredentialResponse {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context?.getString(R.string.web_client_id) ?: throw Error("Context not provided"))
                    .setAutoSelectEnabled(false)
                    .build()
            )
            .build()

        return credentialManager?.getCredential(
            request = request,
            context = context!!
        ) ?: throw Error("Credential manager not defined")
    }
}