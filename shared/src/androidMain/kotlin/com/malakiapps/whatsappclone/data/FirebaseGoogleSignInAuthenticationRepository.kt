package com.malakiapps.whatsappclone.data

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.malakiapps.whatsappclone.R
import com.malakiapps.whatsappclone.domain.common.AuthenticationError
import com.malakiapps.whatsappclone.domain.common.AuthenticationException
import com.malakiapps.whatsappclone.domain.common.AuthenticationUserNotFound
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.UserAuthenticationRepository
import com.malakiapps.whatsappclone.domain.user.UserType
import com.malakiapps.whatsappclone.domain.user.getCurrentUserImplementation
import com.malakiapps.whatsappclone.domain.common.handleOnFailureResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class FirebaseGoogleSignInAuthenticationRepository : UserAuthenticationRepository {
    private var credentialManager: CredentialManager? = null
    override val firebaseAuth = Firebase.auth
    private var context: Context? = null

    override fun initializeCredentialManager(context: Context) {
        credentialManager = CredentialManager.Companion.create(context)
        this.context = context
    }

    override fun getUser(): AuthenticationUser? {
        return firebaseAuth.currentUser?.let { currentUser ->
            AuthenticationUser(
                name = currentUser.displayName ?: "",
                email = if(currentUser.email?.isNotBlank() == true){
                    currentUser.email
                }else {
                    null
                },
                initialImage = currentUser.photoUrl,
                type = UserType.REAL
            )
        }
    }

    override suspend fun signIn(): Response<AuthenticationUser, AuthenticationError> {
        val result = try {
            buildCredentialRequest()
        } catch (e: Exception) {
            e.printStackTrace()

            //Don't prevent the coroutine from being cancelled
            if (e is CancellationException) {
                throw e
            }
            return Response.Failure(AuthenticationException(e.message ?: "Unknown error"))
        }

        return result.handleSignIn()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun anonymousSignIn(): Response<AuthenticationUser, AuthenticationError> {
        return suspendCancellableCoroutine { cont ->
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener { task ->
                    val authenticationUser = AuthenticationUser(
                        name = "Anonymous User",
                        email = null,
                        initialImage = null,
                        type = UserType.ANONYMOUS
                    )
                    cont.resume(Response.Success(authenticationUser), null)
                }
                .addOnFailureListener { error ->
                    cont.handleOnFailureResponse(error)
                }
        }
    }

    override suspend fun signOut() {
        credentialManager?.clearCredentialState(
            ClearCredentialStateRequest()
        )
        firebaseAuth.signOut()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun updateProfile(name: String?): Boolean {
        return suspendCancellableCoroutine { cont ->
            if (name != null) {
                firebaseAuth.currentUser?.let { existingUser ->
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

    override fun getUserState(): Flow<AuthenticationUser?> {
        return getCurrentUserImplementation()
    }

    private suspend fun GetCredentialResponse.handleSignIn(): Response<AuthenticationUser, AuthenticationError> {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val tokenCredential = GoogleIdTokenCredential.Companion.createFrom(credential.data)

                val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                return authResult.user?.let { currentUser ->
                    Response.Success(
                        AuthenticationUser(
                            name = currentUser.displayName ?: "",
                            email = currentUser.email ?: "",
                            initialImage = currentUser.photoUrl,
                            type = UserType.REAL
                        )
                    )
                } ?: Response.Failure(AuthenticationUserNotFound)
            } catch (e: GoogleIdTokenParsingException) {
                return Response.Failure(AuthenticationException(e.message ?: "Unknown error"))
            }
        } else {
            return Response.Failure(AuthenticationException("Incorrect credential"))
        }
    }

    private suspend fun buildCredentialRequest(): GetCredentialResponse {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(
                        context?.getString(R.string.web_client_id)
                            ?: throw Error("Context not provided")
                    )
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