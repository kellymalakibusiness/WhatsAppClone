package com.malakiapps.whatsappclone.domain.user

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

data class Profile(
    val name: Name,
    val email: Email,
    val about: About,
    val image: Image?
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

@JvmInline
value class About(
    val value: String
)