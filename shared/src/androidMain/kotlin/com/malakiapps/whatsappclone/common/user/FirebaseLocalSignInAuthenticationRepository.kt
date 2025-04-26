package com.malakiapps.whatsappclone.common.user

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseLocalSignInAuthenticationRepository: UserAuthenticationRepository {
    override fun initializeCredentialManager(context: Context) = Unit

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun signIn(): User? {
        return suspendCancellableCoroutine { cont ->
            Firebase.auth.createUserWithEmailAndPassword("kellySerdadu@gmail.com", "boobies123")
             .addOnCompleteListener { task ->
                 cont.handleOnCompleteSignIn(task)
             }
             .addOnFailureListener {
                 cont.returnNull()
             }
        }
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
        Firebase.auth.signOut()

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
        return Firebase.auth.currentUser?.let {
            User(
                id = it.uid,
                name = it.displayName ?: "",
                email = it.email ?: "",
                imageUri = it.photoUrl
            )
        }
    }
}