package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.AuthenticationUserState
import com.malakiapps.whatsappclone.domain.user.Initialized
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class InitialAuthenticationCheckUseCase(

) {
    suspend operator fun invoke(userAuthenticationState: StateFlow<AuthenticationUserState>): Response<AuthenticationUser?, Error>{
        //Get our first emitted value
        val userAuthState = userAuthenticationState.filter { it is Initialized }.first() as Initialized

        return Response.Success(userAuthState.value)
    }
}