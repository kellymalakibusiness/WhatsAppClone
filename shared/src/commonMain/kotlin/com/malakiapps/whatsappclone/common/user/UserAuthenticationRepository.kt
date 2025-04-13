package com.malakiapps.whatsappclone.common.user

interface UserAuthenticationRepository {

    suspend fun signIn(): User?

    suspend fun anonymousSignIn(): User?

    suspend fun singOut(): Boolean

    suspend fun updateProfile(name: String?): Boolean

    fun getCurrentUser(): User?
}