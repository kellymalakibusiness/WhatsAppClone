package com.malakiapps.whatsappclone.domain.user

import com.malakiapps.whatsappclone.domain.common.CreateUserError
import com.malakiapps.whatsappclone.domain.common.DeleteUserError
import com.malakiapps.whatsappclone.domain.common.GetUserError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UpdateUserError

expect interface AuthenticatedUserAccountRepository {
    suspend fun createContact(email: Email, authenticationContext: AuthenticationContext): Response<Profile, CreateUserError>

    suspend fun getContact(email: Email): Response<Profile, GetUserError>

    suspend fun getUserDetails(email: Email): Response<UserDetails, GetUserError>

    suspend fun updateContact(userContactUpdate: UserContactUpdate): Response<Profile, UpdateUserError>

    suspend fun updateUserDetails(userDetailsUpdate: UserDetailsUpdate): Response<UserDetails, UpdateUserError>

    suspend fun deleteUser(email: Email): Response<Unit, DeleteUserError>
}