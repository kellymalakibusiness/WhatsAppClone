package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.isSuccess
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserStorageRepository

class InitializeUserUseCase(
    val anonymousUserStorageRepository: UserStorageRepository,
    val userStorageRepository: UserStorageRepository,

) {
    suspend operator fun invoke(authenticationUser: AuthenticationUser): Response<User, Error> {
        //We first try to read the user if they exist
        val availableUser = authenticationUser.email?.let { availableEmail ->
            //Firebase user
            userStorageRepository.getUser(email = availableEmail)
        } ?: run {
            anonymousUserStorageRepository.getUser(email = "anonymous")
        }

        //Check if user item already exists
        return if(availableUser.isSuccess()){
            return availableUser
        } else {
            //It's a new user, we need to create one
            createNewUserItem(authenticationUser)
        }
    }

    suspend fun createNewUserItem(authenticationUser: AuthenticationUser): Response<User, Error> {
        return authenticationUser.email?.let { availableEmail ->
            //Firebase user
            userStorageRepository.createUser(email = availableEmail, authenticationUser = authenticationUser)
        } ?: run {
            anonymousUserStorageRepository.createUser(email = "anonymous", authenticationUser = authenticationUser)
        }
    }
}