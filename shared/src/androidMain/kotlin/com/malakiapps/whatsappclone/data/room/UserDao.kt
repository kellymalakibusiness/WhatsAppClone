package com.malakiapps.whatsappclone.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity WHERE email = :email")
    suspend fun getUser(email: String): UserEntity?

    @Upsert
    suspend fun upsertUser(userEntity: UserEntity)

    @Delete
    suspend fun deleteUser(userEntity: UserEntity)
}