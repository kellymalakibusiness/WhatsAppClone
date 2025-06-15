package com.malakiapps.whatsappclone.domain.user

import com.malakiapps.whatsappclone.domain.common.AuthenticationError
import com.malakiapps.whatsappclone.domain.common.Response
import kotlinx.coroutines.flow.Flow

expect interface UserAuthenticationRepository {

    fun getUser(): AuthenticationUser?

    suspend fun signIn(): Response<AuthenticationUser, AuthenticationError>

    suspend fun anonymousSignIn(): Response<AuthenticationUser, AuthenticationError>

    suspend fun signOut()

    suspend fun updateProfile(name: String?): Boolean

    fun getUserState(): Flow<AuthenticationUser?>
}