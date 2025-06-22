package com.malakiapps.whatsappclone.data

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.malakiapps.whatsappclone.domain.common.AuthenticationError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationRepository
import com.malakiapps.whatsappclone.domain.user.UserType
import com.malakiapps.whatsappclone.domain.user.getCurrentUserImplementation
import com.malakiapps.whatsappclone.domain.common.handleOnCompleteSignIn
import com.malakiapps.whatsappclone.domain.common.handleOnFailureResponse
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.SignInResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseLocalSignInAuthenticationRepository : AuthenticationRepository {
    override val firebaseAuth: FirebaseAuth = Firebase.auth
    override fun initializeCredentialManager(context: Context) = Unit

    override fun getAuthContext(): AuthenticationContext? {
        return firebaseAuth.currentUser?.let { currentUser ->
            AuthenticationContext(
                name = Name(currentUser.displayName ?: ""),
                email = if (currentUser.email?.isNotBlank() == true) {
                    currentUser.email?.let { Email(it) }
                } else {
                    null
                },
                type = UserType.REAL
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun signIn(): Response<SignInResponse, AuthenticationError> {
        return suspendCancellableCoroutine { cont ->
            firebaseAuth.createUserWithEmailAndPassword("kellySerdadu@gmail.com", "boobies123")
                .addOnCompleteListener { task ->
                    cont.handleOnCompleteSignIn(
                        task = task,
                        initialBase64Image = null
                        )
                }
                .addOnFailureListener { error ->
                    cont.handleOnFailureResponse(error)
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun anonymousSignIn(): Response<AuthenticationContext, AuthenticationError> {
        return suspendCancellableCoroutine { cont ->
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener { task ->
                    val authenticationContext = AuthenticationContext(
                        name = Name("Anonymous User"),
                        email = null,
                        type = UserType.ANONYMOUS
                    )
                    cont.resume(Response.Success(authenticationContext), null)
                }
                .addOnFailureListener { error ->
                    cont.handleOnFailureResponse(error)
                }
        }
    }

    override suspend fun signOut() {
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

    override fun getAuthContextState(): Flow<AuthenticationContext?> = getCurrentUserImplementation()
}