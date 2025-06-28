package com.malakiapps.whatsappclone.domain.user

data class UserDetails(
    val type: UserType,
    val contacts: List<Email>,
)

enum class UserType {
    REAL,
    ANONYMOUS
}