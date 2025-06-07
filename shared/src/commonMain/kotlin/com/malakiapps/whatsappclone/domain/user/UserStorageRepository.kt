package com.malakiapps.whatsappclone.domain.user

import com.malakiapps.whatsappclone.common.CreateUserError
import com.malakiapps.whatsappclone.common.DeleteUserError
import com.malakiapps.whatsappclone.common.GetUserError
import com.malakiapps.whatsappclone.common.Response
import com.malakiapps.whatsappclone.common.UpdateUserError

expect interface UserStorageRepository {
    suspend fun createUser(email: String, authenticationUser: AuthenticationUser): Response<User, CreateUserError>

    suspend fun getUser(email: String): Response<User, GetUserError>

    suspend fun updateUser(userUpdate: UserUpdate): Response<User, UpdateUserError>

    suspend fun deleteUser(email: String): Response<Unit, DeleteUserError>
}