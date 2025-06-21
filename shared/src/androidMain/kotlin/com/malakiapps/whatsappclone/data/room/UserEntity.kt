package com.malakiapps.whatsappclone.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserType

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val email: Email,
    val name: Name,
    val about: String,
    val image: Image?,
    val contacts: List<Email>
)


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

class EmailConverter {
    @TypeConverter
    fun from(value: Email): String = value.value

    @TypeConverter
    fun to(value: String): Email = Email(value)
}

class NameConverter {
    @TypeConverter
    fun from(value: Name): String = value.value

    @TypeConverter
    fun to(value: String): Name = Name(value)
}

class ImageConverter {
    @TypeConverter
    fun from(value: Image): String = value.value

    @TypeConverter
    fun to(value: String): Image = Image(value)
}

class ContactsConverter {
    @TypeConverter
    fun fromList(value: List<Email>): String = value.joinToString(",") { it.value }

    @TypeConverter
    fun toList(value: String): List<Email> =
        if (value.isEmpty()) emptyList() else value.split(",").map { Email(it) }
}