package com.malakiapps.whatsappclone.domain.user

import com.malakiapps.whatsappclone.domain.common.CreateUserError
import com.malakiapps.whatsappclone.domain.common.DeleteUserError
import com.malakiapps.whatsappclone.domain.common.GetUserError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UpdateUserError

actual interface AuthenticatedUserAccountRepository {
    actual suspend fun createContact(
        email: Email,
        authenticationContext: AuthenticationContext
    ): Response<Profile, CreateUserError>

    actual suspend fun upgradeContactFromAnonymous(userContactUpdate: UserContactUpdate): Response<Profile, CreateUserError>
    actual suspend fun getContact(email: Email): Response<Profile, GetUserError>
    actual suspend fun getUserDetails(email: Email): Response<UserDetails, GetUserError>
    actual suspend fun updateContact(userContactUpdate: UserContactUpdate): Response<Profile, UpdateUserError>
    actual suspend fun updateUserDetails(userDetailsUpdate: UserDetailsUpdate): Response<UserDetails, UpdateUserError>
    actual suspend fun deleteUser(email: Email): Response<Unit, DeleteUserError>
}