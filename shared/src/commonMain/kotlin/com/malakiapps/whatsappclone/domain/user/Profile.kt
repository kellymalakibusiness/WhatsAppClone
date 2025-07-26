package com.malakiapps.whatsappclone.domain.user

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

data class Profile(
    val name: Name,
    val email: Email,
    val about: About,
    val image: Image?
)

data class SearchProfileResult(
    val profile: Profile,
    val profileType: ProfileType
)

@JvmInline
@Serializable
value class Name(
    val value: String
)

fun Name.addLeadingYou(): Name = Name("$value (You)")

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

@JvmInline
value class TimeValue(
    val value: String
){
    companion object {
        fun LocalTime.toParsedTimeValue() = TimeValue("${hour.addLeadingZero()}:${minute.addLeadingZero()}")
        private fun Int.addLeadingZero(): String {
            return toString().padStart(2, '0')
        }
    }
}

enum class ProfileType {
    OWNER,
    CONTACT,
    NEW,
    UNKNOWN
}