package com.malakiapps.whatsappclone.domain.user

import com.malakiapps.whatsappclone.domain.common.AuthenticationError
import com.malakiapps.whatsappclone.domain.common.Response
import kotlinx.coroutines.flow.Flow

expect interface AuthenticationRepository {

    fun getAuthContext(): AuthenticationContext?

    suspend fun signIn(): Response<SignInResponse, AuthenticationError>

    suspend fun anonymousSignIn(): Response<AuthenticationContext, AuthenticationError>

    suspend fun signOut()

    suspend fun updateProfile(name: String?): Boolean

    fun getAuthContextState(): Flow<AuthenticationContext?>
}