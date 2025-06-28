package com.malakiapps.whatsappclone.domain.common

sealed interface Error

//AUTHENTICATION
sealed interface AuthenticationError: Error

object AuthenticationUserNotFound: AuthenticationError

data class AuthenticationException(
    val message: String
): AuthenticationError

//CREATE USER
sealed interface CreateUserError: AuthenticationError

object EmailNotFound: CreateUserError, GetUserError

data class UnknownError(val exception: Exception): CreateUserError, DeleteUserError

//GET USER
sealed interface GetUserError: Error

object UserNotFound: GetUserError, CreateUserError, UpdateUserError

data class UserParsingError(val key: String): GetUserError

//UPDATE USER
sealed interface UpdateUserError: Error

data class UpdateUserException(val message: String): UpdateUserError

data class InvalidUpdate(val message: String): UpdateUserError


//DELETE USER
sealed interface DeleteUserError: Error

data object ForbiddenRequest: Error
