package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.common.DeleteMessageError
import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Paginate
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.SendMessagesError
import com.malakiapps.whatsappclone.domain.common.UpdateMessageError
import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.coroutines.flow.Flow


expect interface AnonymousUserMessageRepository {

    fun getConversation(owner: Email, limit: Int = MESSAGE_LIMIT): Flow<Response<RawConversation, GetMessagesError>>

    suspend fun getPaginatedConversation(owner: Email, paginate: Paginate, limit: Int = MESSAGE_LIMIT): Response<RawConversation, GetMessagesError>

    suspend fun sendMessage(message: Message): Response<Message, SendMessagesError>

    suspend fun updateMessage(updateMessage: UpdateMessage): Response<Unit, UpdateMessageError>

    suspend fun deleteMessages(owner: Email, messageIds: List<MessageId>): Response<Unit, DeleteMessageError>
    suspend fun exportAllUserMessages(owner: Email): Response<RawConversation, GetMessagesError>
}