package com.malakiapps.whatsappclone.domain.user

actual class User (
    actual val name: String,
    actual val email: String,
    actual val about: String,
    actual val image: String?,
    actual val contacts: List<String>,
    actual val type: UserType
)