package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.common.DeleteMessageError
import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Paginate
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.SendMessagesError
import com.malakiapps.whatsappclone.domain.common.UpdateMessageError
import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.coroutines.flow.Flow

actual interface MessagesRepository {
    actual suspend fun getConversation(
        owner: Email,
        target: Email,
        paginate: Paginate?
    ): Response<Conversation, GetMessagesError>

    actual suspend fun listenForMessagesChanges(
        owner: Email,
        target: Email
    ): Flow<Response<Conversation, GetMessagesError>>

    actual suspend fun listenForNewUserMessages(owner: Email): Flow<Response<List<Pair<MessageUpdateType, Message>>, GetMessagesError>>
    actual suspend fun sendMessage(message: Message): Response<Message, SendMessagesError>
    actual suspend fun updateMessage(updateMessage: UpdateMessage): Response<Unit, UpdateMessageError>
    actual suspend fun deleteMessage(
        owner: Email,
        target: Email,
        messageId: MessageId
    ): Response<Unit, DeleteMessageError>
}