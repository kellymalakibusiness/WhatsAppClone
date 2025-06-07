package com.malakiapps.whatsappclone.domain.user

expect class AuthenticationUser {
    val name: String
    val email: String?
    val type: UserType
}

interface AuthenticationUserState

data object NotInitialized: AuthenticationUserState

data class Initialized(val value: AuthenticationUser?): AuthenticationUserState
