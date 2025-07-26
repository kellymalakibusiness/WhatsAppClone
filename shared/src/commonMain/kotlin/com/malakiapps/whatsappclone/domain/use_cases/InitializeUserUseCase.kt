package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.isSuccess
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AnonymousUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticatedUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.UserDetails
import com.malakiapps.whatsappclone.domain.user.UserType

class InitializeUserUseCase(
    val anonymousUserAccountRepository: AnonymousUserAccountRepository,
    val authenticatedUserAccountRepository: AuthenticatedUserAccountRepository,
) {
    suspend operator fun invoke(authenticationContext: AuthenticationContext): Response<Pair<Profile, UserDetails>, Error> {
        //We first try to read the user if they exist
        val availableUserContact = authenticationContext.email?.let { availableEmail ->
            //Firebase user
            authenticatedUserAccountRepository.getContact(email = availableEmail)
        } ?: run {
            anonymousUserAccountRepository.getContact(email = ANONYMOUS_EMAIL)
        }

        //Check if user item already exists
        val user = if (availableUserContact.isSuccess()) {
            availableUserContact
        } else {
            //It's a new user, we need to create one
            createNewUserItem(authenticationContext)
        }

        when (user) {
            is Response.Failure<Profile, Error> -> {
                return Response.Failure(user.error)
            }

            is Response.Success<Profile, Error> -> {
                val userDetails = getUserDetails(authenticationContext)

                return when (userDetails) {
                    is Response.Failure<UserDetails, Error> -> {
                        Response.Failure(userDetails.error)
                    }

                    is Response.Success<UserDetails, Error> -> {
                        Response.Success(data = Pair(user.data, userDetails.data))
                    }
                }
            }
        }
    }

    private suspend fun createNewUserItem(authenticationContext: AuthenticationContext): Response<Profile, Error> {
        return authenticationContext.email?.let { availableEmail ->
            //Firebase user
            authenticatedUserAccountRepository.createContact(
                email = availableEmail,
                authenticationContext = authenticationContext
            )

        } ?: run {
            anonymousUserAccountRepository.createAccount(
                email = ANONYMOUS_EMAIL,
                authenticationContext = authenticationContext
            )
        }
    }

    private suspend fun getUserDetails(authenticationContext: AuthenticationContext): Response<UserDetails, Error> {
        return authenticationContext.email?.let { availableEmail ->
            authenticatedUserAccountRepository.getUserDetails(email = availableEmail)
        } ?: run {
            Response.Success(
                UserDetails(
                    type = UserType.ANONYMOUS,
                    contacts = emptyList()
                )
            )
        }
    }
}