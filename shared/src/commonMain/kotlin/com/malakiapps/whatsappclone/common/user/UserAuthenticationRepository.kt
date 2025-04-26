package com.malakiapps.whatsappclone.common.user

expect interface UserAuthenticationRepository {

    suspend fun signIn(): User?

    suspend fun anonymousSignIn(): User?

    suspend fun signOut(): Boolean

    suspend fun updateProfile(name: String?): Boolean

    fun getCurrentUser(): User?
}