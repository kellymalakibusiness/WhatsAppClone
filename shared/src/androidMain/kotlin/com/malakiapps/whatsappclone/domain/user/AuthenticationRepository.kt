package com.malakiapps.whatsappclone.domain.user

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.malakiapps.whatsappclone.domain.common.AuthenticationError
import com.malakiapps.whatsappclone.domain.common.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

actual interface AuthenticationRepository {
    val firebaseAuth: FirebaseAuth
    fun initializeCredentialManager(context: Context)

    actual fun getAuthContext(): AuthenticationContext?

    actual suspend fun signIn(): Response<SignInResponse, AuthenticationError>

    actual suspend fun anonymousSignIn(): Response<AuthenticationContext, AuthenticationError>

    actual suspend fun signOut()

    actual suspend fun updateProfile(name: String?): Boolean

    actual fun getAuthContextState(): Flow<AuthenticationContext?>
}

fun AuthenticationRepository.getCurrentUserImplementation(): Flow<AuthenticationContext?> =
    callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { updatedAuth ->
            val currentUser = updatedAuth.currentUser?.let { currentUser ->
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
            trySend(currentUser)
        }

        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }