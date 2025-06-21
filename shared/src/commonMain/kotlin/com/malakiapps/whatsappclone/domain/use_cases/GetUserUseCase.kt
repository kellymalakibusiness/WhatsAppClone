package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserStorageRepository

class GetUserUseCase(
    val userStorageRepository: UserStorageRepository,
    val anonymousUserStorageRepository: UserStorageRepository,
) {
    suspend operator fun invoke(authenticationContext: AuthenticationContext): Response<User, Error> {
            //Authenticated
            //First check if the user has an account email
            return authenticationContext.email?.let { existingEmail ->
                //Email user
                //Read the user item
                userStorageRepository.getUser(email = existingEmail)
            } ?: run {
                //Anonymous account
                //Read anonymous user item
                anonymousUserStorageRepository.getUser(email = ANONYMOUS_EMAIL)
            }
    }
}