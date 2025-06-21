package com.malakiapps.whatsappclone.domain.user

import com.malakiapps.whatsappclone.domain.common.CreateUserError
import com.malakiapps.whatsappclone.domain.common.DeleteUserError
import com.malakiapps.whatsappclone.domain.common.GetUserError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UpdateUserError

actual interface UserStorageRepository {
    actual suspend fun createUser(email: Email, authenticationContext: AuthenticationContext): Response<User, CreateUserError>

    actual suspend fun getUser(email: Email): Response<User, GetUserError>

    actual suspend fun updateUser(userUpdate: UserUpdate): Response<User, UpdateUserError>

    actual suspend fun deleteUser(email: Email): Response<Unit, DeleteUserError>
}