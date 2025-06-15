package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.UserAuthenticationRepository

class SignInUseCase(
    val authenticationRepository: UserAuthenticationRepository
) {

    suspend fun signInWithGoogle(): Response<AuthenticationUser, Error>{
        return authenticationRepository.signIn()
    }

    suspend fun signInAnonymously(): Response<AuthenticationUser, Error>{
        return authenticationRepository.anonymousSignIn()
    }
}