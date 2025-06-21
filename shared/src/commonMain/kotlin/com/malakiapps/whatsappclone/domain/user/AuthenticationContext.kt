package com.malakiapps.whatsappclone.domain.user

data class AuthenticationContext(
    val name: Name,
    val email: Email?,
    val type: UserType
)

data class SignInResponse(
    val authenticationContext: AuthenticationContext,
    val initialBase64ProfileImage: Image?
)

sealed interface AuthenticationContextState

data object NotInitialized: AuthenticationContextState

data class Initialized(val value: AuthenticationContext?): AuthenticationContextState

val ANONYMOUS_EMAIL = Email("anonymous")