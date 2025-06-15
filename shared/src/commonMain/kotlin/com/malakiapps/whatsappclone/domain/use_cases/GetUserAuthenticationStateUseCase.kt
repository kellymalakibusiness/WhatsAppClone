package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.AuthenticationError
import com.malakiapps.whatsappclone.domain.common.AuthenticationUserNotFound
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.Initialized
import com.malakiapps.whatsappclone.domain.user.UserAuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetUserAuthenticationStateUseCase(
    val authenticationRepository: UserAuthenticationRepository,
) {
    operator fun invoke(): Flow<Initialized> {
        return authenticationRepository
            .getUserState()
            .map {
                Initialized(it)
            }
    }

    fun getAuthUser(): Response<AuthenticationUser, AuthenticationError> {
        return authenticationRepository.getUser()?.let {
            Response.Success(it)
        } ?: Response.Failure(AuthenticationUserNotFound)
    }
}