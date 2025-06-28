package com.malakiapps.whatsappclone.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Profile

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    val email: Email,
    val name: Name,
    val about: About,
    val image: Image?,
)


fun UserEntity.toUser(): Profile {
    return Profile(
        email = email,
        name = name,
        about = about,
        image = image,
    )
}

fun Profile.toUserEntity(): UserEntity {
    return UserEntity(
        email = email,
        name = name,
        about = about,
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

class AboutConverter {
    @TypeConverter
    fun from(value: About): String = value.value

    @TypeConverter
    fun to(value: String): About = About(value)
}