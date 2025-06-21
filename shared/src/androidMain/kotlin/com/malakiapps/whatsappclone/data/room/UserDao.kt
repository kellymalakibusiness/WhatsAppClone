package com.malakiapps.whatsappclone.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.malakiapps.whatsappclone.domain.user.Email

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity WHERE email = :email")
    suspend fun getUser(email: Email): UserEntity?

    @Upsert
    suspend fun upsertUser(userEntity: UserEntity)

    @Delete
    suspend fun deleteUser(userEntity: UserEntity)
}