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

val ANONYMOUS_EMAIL = Email("anonymous")