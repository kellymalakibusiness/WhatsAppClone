package com.malakiapps.whatsappclone.common.user

import android.content.Context

actual interface UserAuthenticationRepository {
    fun initializeCredentialManager(context: Context)

    actual suspend fun signIn(): User?

    actual suspend fun anonymousSignIn(): User?

    actual suspend fun signOut(): Boolean

    actual suspend fun updateProfile(name: String?): Boolean

    actual fun getCurrentUser(): User?
}