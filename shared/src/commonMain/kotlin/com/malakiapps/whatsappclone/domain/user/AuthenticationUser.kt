package com.malakiapps.whatsappclone.domain.user

data class AuthenticationUser(
    val name: String,
    val email: String?,
    val type: UserType
)

data class SignInResponse(
    val authenticationUser: AuthenticationUser,
    val initialBase64ProfileImage: String?
)

sealed interface AuthenticationUserState

data object NotInitialized: AuthenticationUserState

data class Initialized(val value: AuthenticationUser?): AuthenticationUserState
