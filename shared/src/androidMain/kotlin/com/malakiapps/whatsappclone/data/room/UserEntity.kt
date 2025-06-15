package com.malakiapps.whatsappclone.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserType

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val email: String,
    val name: String,
    val about: String,
    val image: String?,
    val contacts: List<String>
)

class ContactsConverter {
    @TypeConverter
    fun fromList(value: List<String>): String = value.joinToString(",")

    @TypeConverter
    fun toList(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split(",")
}


fun UserEntity.toUser(): User {
    return User(
        email = email,
        name = name,
        about = about,
        contacts = contacts,
        image = image,
        type = UserType.ANONYMOUS
    )
}

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        email = email,
        name = name,
        about = about,
        contacts = contacts,
        image = image
    )
}