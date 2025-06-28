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
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.AnonymousUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Some
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.UserContactUpdate

class AnonymousLocalUserAccountRepository(
    private val userDao: UserDao
) : AnonymousUserAccountRepository {
    override suspend fun createAccount(
        email: Email,
        authenticationContext: AuthenticationContext
    ): Response<Profile, CreateUserError> {
        val user = authenticationContext.toUser(email = ANONYMOUS_EMAIL)
        return try {
            userDao.upsertUser(userEntity = user.toUserEntity())
            Response.Success(user)
        } catch (e: Exception) {
            Response.Failure(UnknownError(e))
        }
    }

    override suspend fun getContact(email: Email): Response<Profile, GetUserError> {
        val user = userDao.getUser(email)

        return user?.toUser()?.let {
            Response.Success(it)
        } ?: run {
            Response.Failure(UserNotFound)
        }
    }

    override suspend fun updateAccount(userContactUpdate: UserContactUpdate): Response<Profile, UpdateUserError> {
        val user = userDao.getUser(userContactUpdate.email)

        return user?.let {
            val updatedUser = userContactUpdate.toUpdatedUser(it)
            userDao.upsertUser(updatedUser)

            Response.Success(updatedUser.toUser())
        } ?: run {
            Response.Failure(UserNotFound)
        }
    }

    override suspend fun deleteAccount(email: Email): Response<Unit, DeleteUserError> {
        userDao.getUser(email)?.let {
            userDao.deleteUser(it)
        }

        return Response.Success(Unit)
    }

    private fun AuthenticationContext.toUser(email: Email): Profile {
        return Profile(
            email = email,
            name = name,
            about = About("Hey there! I am using FakeWhatsApp."),
            image = null,
        )
    }

    private fun UserContactUpdate.toUpdatedUser(oldUser: UserEntity): UserEntity {
        return UserEntity(
            email = email,
            name = if (name is Some) {
                name.value
            } else {
                oldUser.name
            },
            about = if (about is Some) {
                about.value
            } else {
                oldUser.about
            },
            image = if (image is Some) {
                image.value
            } else {
                oldUser.image
            }
        )
    }
}