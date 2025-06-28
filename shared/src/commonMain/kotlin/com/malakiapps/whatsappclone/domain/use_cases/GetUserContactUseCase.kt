package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AnonymousUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticatedUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Profile

class GetUserContactUseCase(
    val anonymousUserAccountRepository: AnonymousUserAccountRepository,
    val authenticatedUserAccountRepository: AuthenticatedUserAccountRepository,
) {
    suspend operator fun invoke(authenticationContext: AuthenticationContext): Response<Profile, Error> {
            //Authenticated
            //First check if the user has an account email
            return authenticationContext.email?.let { existingEmail ->
                //Email user
                //Read the user item
                authenticatedUserAccountRepository.getContact(email = existingEmail)
            } ?: run {
                //Anonymous account
                //Read anonymous user item
                anonymousUserAccountRepository.getContact(email = ANONYMOUS_EMAIL)
            }
    }
}