package com.malakiapps.whatsappclone.domain.user

import com.malakiapps.whatsappclone.common.CreateUserError
import com.malakiapps.whatsappclone.common.DeleteUserError
import com.malakiapps.whatsappclone.common.GetUserError
import com.malakiapps.whatsappclone.common.Response
import com.malakiapps.whatsappclone.common.UpdateUserError
import com.malakiapps.whatsappclone.domain.user.room.UserDao
import com.malakiapps.whatsappclone.common.UnknownError
import com.malakiapps.whatsappclone.common.UserNotFound
import com.malakiapps.whatsappclone.domain.user.room.UserEntity
import com.malakiapps.whatsappclone.domain.user.room.toUser
import com.malakiapps.whatsappclone.domain.user.room.toUserEntity

const val EMAIL_VALUE = "anonymous"
class AnonymousLocalUserStorageRepository(
    private val userDao: UserDao
) : UserStorageRepository {
    override suspend fun createUser(
        email: String,
        authenticationUser: AuthenticationUser
    ): Response<User, CreateUserError> {
        val user = authenticationUser.toUser(email = EMAIL_VALUE)
        return try {
            userDao.upsertUser(userEntity = user.toUserEntity())
            Response.Success(user)
        } catch (e: Exception) {
            Response.Failure(UnknownError(e))
        }
    }

    override suspend fun getUser(email: String): Response<User, GetUserError> {
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

    override suspend fun deleteUser(email: String): Response<Unit, DeleteUserError> {
        userDao.getUser(email)?.let {
            userDao.deleteUser(it)
        }

        return Response.Success(Unit)
    }

    private fun AuthenticationUser.toUser(email: String): User {
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
            if (removeContact.second){
                addAll(oldUser.contacts.filter { it != removeContact.first })
            } else {
                addAll(oldUser.contacts)
            }
            if(addContact.second){
                add(addContact.first)
            }
        }
        return UserEntity(
            email = email,
            name = if (name.second) {
                name.first
            } else {
                oldUser.name
            },
            about = if (about.second) {
                about.first
            } else {
                oldUser.about
            },
            contacts = contactsUpdate,
            image = if (image.second) {
                image.first
            } else {
                oldUser.image
            }
        )
    }
}