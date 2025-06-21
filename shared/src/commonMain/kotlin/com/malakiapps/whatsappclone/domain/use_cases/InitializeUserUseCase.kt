package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.isSuccess
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserStorageRepository

class InitializeUserUseCase(
    val anonymousUserStorageRepository: UserStorageRepository,
    val userStorageRepository: UserStorageRepository,

) {
    suspend operator fun invoke(authenticationContext: AuthenticationContext): Response<User, Error> {
        //We first try to read the user if they exist
        val availableUser = authenticationContext.email?.let { availableEmail ->
            //Firebase user
            userStorageRepository.getUser(email = availableEmail)
        } ?: run {
            anonymousUserStorageRepository.getUser(email = ANONYMOUS_EMAIL)
        }

        //Check if user item already exists
        return if(availableUser.isSuccess()){
            return availableUser
        } else {
            //It's a new user, we need to create one
            createNewUserItem(authenticationContext)
        }
    }

    suspend fun createNewUserItem(authenticationContext: AuthenticationContext): Response<User, Error> {
        return authenticationContext.email?.let { availableEmail ->
            //Firebase user
            userStorageRepository.createUser(email = availableEmail, authenticationContext = authenticationContext)
        } ?: run {
            anonymousUserStorageRepository.createUser(email = ANONYMOUS_EMAIL, authenticationContext = authenticationContext)
        }
    }
}