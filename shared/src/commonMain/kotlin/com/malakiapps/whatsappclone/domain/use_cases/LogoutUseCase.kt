package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.user.UserAuthenticationRepository

class LogoutUseCase(
    val authenticationRepository: UserAuthenticationRepository,
) {

    suspend operator fun invoke(){
        authenticationRepository.signOut()
    }
}