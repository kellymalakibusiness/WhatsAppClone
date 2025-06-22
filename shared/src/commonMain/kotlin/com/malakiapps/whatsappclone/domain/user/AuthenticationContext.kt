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

data object InitialLoading: AuthenticationContextState

data class HasValue(val value: AuthenticationContext?): AuthenticationContextState{
    fun isValid(): Boolean {
        return value != null
    }
}

val ANONYMOUS_EMAIL = Email("anonymous")