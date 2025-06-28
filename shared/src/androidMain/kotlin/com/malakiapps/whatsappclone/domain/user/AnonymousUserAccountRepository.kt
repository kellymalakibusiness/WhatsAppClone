package com.malakiapps.whatsappclone.domain.user

import com.malakiapps.whatsappclone.domain.common.CreateUserError
import com.malakiapps.whatsappclone.domain.common.DeleteUserError
import com.malakiapps.whatsappclone.domain.common.GetUserError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UpdateUserError

actual interface AnonymousUserAccountRepository {
    actual suspend fun createAccount(email: Email, authenticationContext: AuthenticationContext): Response<Profile, CreateUserError>

    actual suspend fun getContact(email: Email): Response<Profile, GetUserError>

    actual suspend fun updateAccount(userContactUpdate: UserContactUpdate): Response<Profile, UpdateUserError>

    actual suspend fun deleteAccount(email: Email): Response<Unit, DeleteUserError>
}