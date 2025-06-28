package com.malakiapps.whatsappclone.domain.user

import com.malakiapps.whatsappclone.domain.common.CreateUserError
import com.malakiapps.whatsappclone.domain.common.DeleteUserError
import com.malakiapps.whatsappclone.domain.common.GetUserError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UpdateUserError

expect interface AnonymousUserAccountRepository {
    suspend fun createAccount(email: Email, authenticationContext: AuthenticationContext): Response<Profile, CreateUserError>

    suspend fun getContact(email: Email): Response<Profile, GetUserError>

    suspend fun updateAccount(userContactUpdate: UserContactUpdate): Response<Profile, UpdateUserError>

    suspend fun deleteAccount(email: Email): Response<Unit, DeleteUserError>
}