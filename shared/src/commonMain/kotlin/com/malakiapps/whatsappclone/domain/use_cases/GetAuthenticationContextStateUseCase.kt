package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.AuthenticationError
import com.malakiapps.whatsappclone.domain.common.AuthenticationUserNotFound
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Initialized
import com.malakiapps.whatsappclone.domain.user.UserAuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAuthenticationContextStateUseCase(
    val authenticationRepository: UserAuthenticationRepository,
) {
    operator fun invoke(): Flow<Initialized> {
        return authenticationRepository
            .getAuthContextState()
            .map {
                Initialized(it)
            }
    }

    fun getAuthContext(): Response<AuthenticationContext, AuthenticationError> {
        return authenticationRepository.getAuthContext()?.let {
            Response.Success(it)
        } ?: Response.Failure(AuthenticationUserNotFound)
    }
}