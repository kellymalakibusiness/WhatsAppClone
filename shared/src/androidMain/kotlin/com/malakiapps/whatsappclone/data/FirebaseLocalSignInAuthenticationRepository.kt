package com.malakiapps.whatsappclone.data

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.malakiapps.whatsappclone.domain.common.AuthenticationError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.UserAuthenticationRepository
import com.malakiapps.whatsappclone.domain.user.UserType
import com.malakiapps.whatsappclone.domain.user.getCurrentUserImplementation
import com.malakiapps.whatsappclone.domain.common.handleOnCompleteSignIn
import com.malakiapps.whatsappclone.domain.common.handleOnFailureResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseLocalSignInAuthenticationRepository : UserAuthenticationRepository {
    override val firebaseAuth: FirebaseAuth = Firebase.auth
    override fun initializeCredentialManager(context: Context) = Unit

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

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun signIn(): Response<AuthenticationUser, AuthenticationError> {
        return suspendCancellableCoroutine { cont ->
            firebaseAuth.createUserWithEmailAndPassword("kellySerdadu@gmail.com", "boobies123")
                .addOnCompleteListener { task ->
                    cont.handleOnCompleteSignIn(task)
                }
                .addOnFailureListener { error ->
                    cont.handleOnFailureResponse(error)
                }
        }
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

    override fun getUserState(): Flow<AuthenticationUser?> = getCurrentUserImplementation()
}