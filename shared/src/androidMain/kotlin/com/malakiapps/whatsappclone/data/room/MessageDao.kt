package com.malakiapps.whatsappclone.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.malakiapps.whatsappclone.domain.messages.UpdateMessage
import com.malakiapps.whatsappclone.domain.user.Email

@Dao
interface MessageDao {
    @Query("")
    suspend fun getConversation(email: Email): List<MessageEntity>

    @Upsert
    suspend fun sendMessage(message: MessageEntity): MessageEntity

    @Update
    suspend fun updateMessage(updateMessage: UpdateMessage)

    @Delete
    suspend fun deleteMessage(owner: Email, timeId: String)
}