package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.common.DeleteMessageError
import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Paginate
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.SendMessagesError
import com.malakiapps.whatsappclone.domain.common.UpdateMessageError
import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.coroutines.flow.Flow

actual interface AnonymousUserMessageRepository {
    actual fun getConversation(owner: Email, limit: Int): Flow<Response<RawConversation, GetMessagesError>>

    actual suspend fun getPaginatedConversation(owner: Email, paginate: Paginate, limit: Int): Response<RawConversation, GetMessagesError>

    actual suspend fun sendMessage(message: Message): Response<Message, SendMessagesError>
    actual suspend fun updateMessage(updateMessage: UpdateMessage): Response<Unit, UpdateMessageError>
    actual suspend fun deleteMessages(owner: Email, messageIds: List<MessageId>): Response<Unit, DeleteMessageError>

    actual suspend fun exportAllUserMessages(owner: Email): Response<RawConversation, GetMessagesError>

}