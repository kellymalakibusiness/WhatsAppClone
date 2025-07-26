package com.malakiapps.whatsappclone.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM MessageEntity ORDER BY time DESC LIMIT :limit")
    fun getConversation(limit: Long): Flow<List<MessageEntity>>

    @Query("""
        SELECT * FROM MessageEntity 
        WHERE messageId < :lastMessageId
        ORDER BY time DESC
        LIMIT :limit
    """)
    suspend fun getMessagesFrom(lastMessageId: Int, limit: Long): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun sendMessage(message: MessageEntity)

    @Query("DELETE FROM MessageEntity WHERE messageId IN (:messageIds)")
    suspend fun deleteMessages(messageIds: List<Int>)

    @Query("""
        UPDATE MessageEntity 
        SET value = :newValue, updated = 1 
        WHERE messageId = :messageId
    """)
    suspend fun changeMessageBody(messageId: Int, newValue: String)

    @Query("""
        UPDATE MessageEntity 
        SET senderReaction = :reaction 
        WHERE messageId = :messageId
    """)
    suspend fun reactToMessage(messageId: Int, reaction: String)

    @Query("SELECT * FROM MessageEntity")
    suspend fun exportUserMessages(): List<MessageEntity>
}