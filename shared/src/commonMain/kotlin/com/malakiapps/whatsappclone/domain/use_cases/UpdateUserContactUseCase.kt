package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.InvalidUpdate
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AnonymousUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticatedUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.UserContactUpdate
import com.malakiapps.whatsappclone.domain.user.isNameUpdateValid
import com.malakiapps.whatsappclone.domain.user.isAboutUpdateValid
import com.malakiapps.whatsappclone.domain.user.isImageUpdateValid

class UpdateUserContactUseCase(
    private val anonymousUserAccountRepository: AnonymousUserAccountRepository,
    private val authenticatedUserAccountRepository: AuthenticatedUserAccountRepository,
) {

    suspend operator fun invoke(authenticationContext: AuthenticationContext, userContactUpdate: UserContactUpdate): Response<Profile, Error>{
        //Check if the update is valid
        if(!userContactUpdate.name.isNameUpdateValid()){
            return Response.Failure(InvalidUpdate("Unsupported character length for name"))
        }
        if(!userContactUpdate.about.isAboutUpdateValid()){
            return Response.Failure(InvalidUpdate("Unsupported character length for about"))
        }
        if(!userContactUpdate.image.isImageUpdateValid()){
            return Response.Failure(InvalidUpdate("Unsupported image type"))
        }
        return if (authenticationContext.email != null) {
            //Real user update
            authenticatedUserAccountRepository.updateContact(
                userContactUpdate = userContactUpdate
            )
        } else {
            //Anonymous user update
            anonymousUserAccountRepository.updateAccount(
                userContactUpdate = userContactUpdate.copy(email = ANONYMOUS_EMAIL)
            )
        }
    }
}