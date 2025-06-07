package com.malakiapps.whatsappclone.domain.user

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.malakiapps.whatsappclone.common.AuthenticationError
import com.malakiapps.whatsappclone.common.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

actual interface UserAuthenticationRepository {
    val firebaseAuth: FirebaseAuth
    fun initializeCredentialManager(context: Context)

    actual suspend fun signIn(): Response<AuthenticationUser, AuthenticationError>

    actual suspend fun anonymousSignIn(): Response<AuthenticationUser, AuthenticationError>

    actual suspend fun signOut()

    actual suspend fun updateProfile(name: String?): Boolean

    actual fun getCurrentUser(): Flow<AuthenticationUser?>
}

fun UserAuthenticationRepository.getCurrentUserImplementation(): Flow<AuthenticationUser?> =
    callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { updatedAuth ->
            val currentUser = updatedAuth.currentUser?.let { currentUser ->
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
            trySend(currentUser)
        }

        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }