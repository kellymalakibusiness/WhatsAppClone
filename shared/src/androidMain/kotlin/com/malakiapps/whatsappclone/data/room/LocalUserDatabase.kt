package com.malakiapps.whatsappclone.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [UserEntity::class],
    version = 1
)
@TypeConverters(ContactsConverter::class)
abstract class LocalUserDatabase: RoomDatabase() {
    abstract val dao: UserDao
}