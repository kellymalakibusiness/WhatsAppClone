package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.InvalidUpdate
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserStorageRepository
import com.malakiapps.whatsappclone.domain.user.UserUpdate
import com.malakiapps.whatsappclone.domain.user.isNameUpdateValid
import com.malakiapps.whatsappclone.domain.user.isAboutUpdateValid
import com.malakiapps.whatsappclone.domain.user.isImageUpdateValid

class UpdateUserUseCase(
    val userStorageRepository: UserStorageRepository,
    val anonymousUserStorageRepository: UserStorageRepository,
) {

    suspend operator fun invoke(authenticationContext: AuthenticationContext, userUpdate: UserUpdate): Response<User, Error>{
        //Check if the update is valid
        if(!userUpdate.name.isNameUpdateValid()){
            return Response.Failure(InvalidUpdate("Unsupported character length for name"))
        }
        if(!userUpdate.about.isAboutUpdateValid()){
            return Response.Failure(InvalidUpdate("Unsupported character length for about"))
        }
        if(!userUpdate.image.isImageUpdateValid()){
            return Response.Failure(InvalidUpdate("Unsupported image type"))
        }
        return if (authenticationContext.email != null) {
            //Real user update
            userStorageRepository.updateUser(
                userUpdate = userUpdate
            )
        } else {
            //Anonymous user update
            anonymousUserStorageRepository.updateUser(
                userUpdate = userUpdate.copy(email = ANONYMOUS_EMAIL)
            )
        }
    }
}