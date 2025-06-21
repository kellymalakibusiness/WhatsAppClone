package com.malakiapps.whatsappclone.domain.user

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

data class User (
    val name: Name,
    val email: Email,
    val about: String,
    val image: Image?,
    val contacts: List<Email>,
    val type: UserType
    )

@JvmInline
@Serializable
value class Name(
    val value: String
)

@JvmInline
@Serializable
value class Email(
    val value: String
)

@JvmInline
@Serializable
value class Image(
    val value: String
)

enum class UserType {
    REAL,
    ANONYMOUS
}