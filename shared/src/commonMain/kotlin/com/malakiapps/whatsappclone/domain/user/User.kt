package com.malakiapps.whatsappclone.domain.user

data class User (
    val name: String,
    val email: String,
    val about: String,
    val image: String?,
    val contacts: List<String>,
    val type: UserType
    )

enum class UserType {
    REAL,
    ANONYMOUS
}