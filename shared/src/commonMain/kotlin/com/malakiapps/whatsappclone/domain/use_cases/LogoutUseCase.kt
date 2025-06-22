package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.user.AuthenticationRepository

class LogoutUseCase(
    val authenticationRepository: AuthenticationRepository,
) {

    suspend operator fun invoke(){
        authenticationRepository.signOut()
    }
}