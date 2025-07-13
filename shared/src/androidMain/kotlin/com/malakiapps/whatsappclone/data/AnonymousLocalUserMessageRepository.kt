package com.malakiapps.whatsappclone.data

import com.malakiapps.whatsappclone.data.room.MessageDao
import com.malakiapps.whatsappclone.domain.common.DeleteMessageError
import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.SendMessagesError
import com.malakiapps.whatsappclone.domain.common.UpdateMessageError
import com.malakiapps.whatsappclone.domain.contacts.AnonymousUserMessageRepository
import com.malakiapps.whatsappclone.domain.messages.Conversation
import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.UpdateMessage
import com.malakiapps.whatsappclone.domain.user.Email

class AnonymousLocalUserMessageRepository(
    private val messageDao: MessageDao
): AnonymousUserMessageRepository {
    override suspend fun getConversation(owner: Email): Response<Conversation, GetMessagesError> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(message: Message): Response<Message, SendMessagesError> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMessage(updateMessage: UpdateMessage): Response<Message, UpdateMessageError> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMessage(
        owner: Email,
        timeId: String
    ): Response<Unit, DeleteMessageError> {
        TODO("Not yet implemented")
    }
}