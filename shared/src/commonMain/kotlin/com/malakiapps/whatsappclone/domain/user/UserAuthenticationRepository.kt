package com.malakiapps.whatsappclone.domain.user

import com.malakiapps.whatsappclone.common.AuthenticationError
import com.malakiapps.whatsappclone.common.Response
import kotlinx.coroutines.flow.Flow

expect interface UserAuthenticationRepository {

    suspend fun signIn(): Response<AuthenticationUser, AuthenticationError>

    suspend fun anonymousSignIn(): Response<AuthenticationUser, AuthenticationError>

    suspend fun signOut()

    suspend fun updateProfile(name: String?): Boolean

    fun getCurrentUser(): Flow<AuthenticationUser?>
}