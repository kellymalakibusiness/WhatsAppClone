package com.malakiapps.whatsappclone.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [UserEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(EmailConverter::class, NameConverter::class, ImageConverter::class, AboutConverter::class, MessageValueConverter::class, LocalDateTimeConverter::class)
abstract class LocalUserDatabase: RoomDatabase() {
    abstract val dao: UserDao
    abstract val messagesDao: MessageDao
}