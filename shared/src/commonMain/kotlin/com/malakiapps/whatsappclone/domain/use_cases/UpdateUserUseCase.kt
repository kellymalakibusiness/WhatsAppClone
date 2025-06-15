package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserStorageRepository
import com.malakiapps.whatsappclone.domain.user.UserUpdate

class UpdateUserUseCase(
    val userStorageRepository: UserStorageRepository,
    val anonymousUserStorageRepository: UserStorageRepository,
) {

    suspend operator fun invoke(authenticationUser: AuthenticationUser, userUpdate: UserUpdate): Response<User, Error>{
        return if (authenticationUser.email != null) {
            //Real user update
            userStorageRepository.updateUser(
                userUpdate = userUpdate
            )
        } else {
            //Anonymous user update
            anonymousUserStorageRepository.updateUser(
                userUpdate = userUpdate.copy(email = "anonymous")
            )
        }
    }
}