package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.StateValue
import com.malakiapps.whatsappclone.domain.user.UserState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class InitialAuthenticationCheckUseCase(

) {
    suspend operator fun invoke(userAuthenticationState: StateFlow<UserState<AuthenticationContext?>>): Response<AuthenticationContext?, Error>{
        //Get our first emitted value
        val userAuthState = userAuthenticationState.filter { it is StateValue }.first() as StateValue

        return Response.Success(userAuthState.value)
    }
}