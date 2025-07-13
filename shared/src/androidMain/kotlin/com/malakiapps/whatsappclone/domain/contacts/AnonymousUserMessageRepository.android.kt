package com.malakiapps.whatsappclone.domain.contacts

import com.malakiapps.whatsappclone.domain.common.DeleteMessageError
import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.SendMessagesError
import com.malakiapps.whatsappclone.domain.common.UpdateMessageError
import com.malakiapps.whatsappclone.domain.messages.Conversation
import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.UpdateMessage
import com.malakiapps.whatsappclone.domain.user.Email

actual interface AnonymousUserMessageRepository {
    actual suspend fun getConversation(owner: Email): Response<Conversation, GetMessagesError>
    actual suspend fun sendMessage(message: Message): Response<Message, SendMessagesError>
    actual suspend fun updateMessage(updateMessage: UpdateMessage): Response<Message, UpdateMessageError>
    actual suspend fun deleteMessage(
        owner: Email,
        timeId: String
    ): Response<Unit, DeleteMessageError>

}