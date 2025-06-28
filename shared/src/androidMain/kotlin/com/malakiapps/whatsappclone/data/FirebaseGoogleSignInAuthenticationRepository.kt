package com.malakiapps.whatsappclone.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.malakiapps.whatsappclone.domain.user.AuthenticationRepository
import com.malakiapps.whatsappclone.domain.user.UserType
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.getCurrentUserImplementation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseGoogleSignInAuthenticationRepository : AuthenticationRepository {
    override val firebaseAuth = Firebase.auth

    override fun getAuthContext(): AuthenticationContext? {
        return firebaseAuth.currentUser?.let { currentUser ->
            AuthenticationContext(
                name = Name(currentUser.displayName ?: ""),
                email = if(currentUser.email?.isNotBlank() == true){
                    currentUser.email?.let { Email(it) }
                }else {
                    null
                },
                type = UserType.REAL
            )
        }
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

    override fun getAuthContextState(): Flow<AuthenticationContext?> {
        return getCurrentUserImplementation()
    }
}