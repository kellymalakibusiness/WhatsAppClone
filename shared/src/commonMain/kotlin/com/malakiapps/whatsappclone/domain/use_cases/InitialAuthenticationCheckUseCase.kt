package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.AuthenticationContextState
import com.malakiapps.whatsappclone.domain.user.HasValue
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class InitialAuthenticationCheckUseCase(

) {
    suspend operator fun invoke(userAuthenticationState: StateFlow<AuthenticationContextState>): Response<AuthenticationContext?, Error>{
        //Get our first emitted value
        val userAuthState = userAuthenticationState.filter { it is HasValue }.first() as HasValue

        return Response.Success(userAuthState.value)
    }
}