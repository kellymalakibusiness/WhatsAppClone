package com.malakiapps.whatsappclone.data

import com.malakiapps.whatsappclone.domain.common.CreateUserError
import com.malakiapps.whatsappclone.domain.common.DeleteUserError
import com.malakiapps.whatsappclone.domain.common.GetUserError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UpdateUserError
import com.malakiapps.whatsappclone.domain.common.UnknownError
import com.malakiapps.whatsappclone.domain.common.UserNotFound
import com.malakiapps.whatsappclone.data.room.UserDao
import com.malakiapps.whatsappclone.data.room.UserEntity
import com.malakiapps.whatsappclone.data.room.toUser
import com.malakiapps.whatsappclone.data.room.toUserEntity
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Update
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserStorageRepository
import com.malakiapps.whatsappclone.domain.user.UserType
import com.malakiapps.whatsappclone.domain.user.UserUpdate

class AnonymousLocalUserStorageRepository(
    private val userDao: UserDao
) : UserStorageRepository {
    override suspend fun createUser(
        email: Email,
        authenticationContext: AuthenticationContext
    ): Response<User, CreateUserError> {
        val user = authenticationContext.toUser(email = ANONYMOUS_EMAIL)
        return try {
            userDao.upsertUser(userEntity = user.toUserEntity())
            Response.Success(user)
        } catch (e: Exception) {
            Response.Failure(UnknownError(e))
        }
    }

    override suspend fun getUser(email: Email): Response<User, GetUserError> {
        val user = userDao.getUser(email)

        return user?.toUser()?.let {
            Response.Success(it)
        } ?: run {
            Response.Failure(UserNotFound)
        }
    }

    override suspend fun updateUser(userUpdate: UserUpdate): Response<User, UpdateUserError> {
        val user = userDao.getUser(userUpdate.email)

        return user?.let {
            val updatedUser = userUpdate.toUpdatedUser(it)
            userDao.upsertUser(updatedUser)

            Response.Success(updatedUser.toUser())
        } ?: run {
            Response.Failure(UserNotFound)
        }
    }

    override suspend fun deleteUser(email: Email): Response<Unit, DeleteUserError> {
        userDao.getUser(email)?.let {
            userDao.deleteUser(it)
        }

        return Response.Success(Unit)
    }

    private fun AuthenticationContext.toUser(email: Email): User {
        return User(
            email = email,
            name = name,
            about = "Hey there! I am using WhatsApp.",
            contacts = emptyList(),
            image = null,
            type = UserType.ANONYMOUS
        )
    }

    private fun UserUpdate.toUpdatedUser(oldUser: UserEntity): UserEntity {
        val contactsUpdate = buildList {
            if (removeContact is Update){
                addAll(oldUser.contacts.filter { it != removeContact.value })
            } else {
                addAll(oldUser.contacts)
            }
            if(addContact is Update){
                add(addContact.value)
            }
        }
        return UserEntity(
            email = email,
            name = if (name is Update) {
                name.value
            } else {
                oldUser.name
            },
            about = if (about is Update) {
                about.value
            } else {
                oldUser.about
            },
            contacts = contactsUpdate,
            image = if (image is Update) {
                image.value
            } else {
                oldUser.image
            }
        )
    }
}