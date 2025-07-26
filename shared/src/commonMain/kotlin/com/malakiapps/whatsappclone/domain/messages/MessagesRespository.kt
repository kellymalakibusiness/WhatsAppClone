package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.common.DeleteMessageError
import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Paginate
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.SendMessagesError
import com.malakiapps.whatsappclone.domain.common.UpdateMessageError
import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.coroutines.flow.Flow

const val MESSAGE_LIMIT = 20
expect interface MessagesRepository {
    suspend fun getAllActiveConversations(owner: Email): Response<List<ConversationBrief>, GetMessagesError>

    suspend fun getConversation(owner: Email, target: Email, limit: Int = MESSAGE_LIMIT, paginate: Paginate? = null): Response<RawConversation, GetMessagesError>

    fun listenForMessagesChanges(owner: Email, target: Email, limit: Int = MESSAGE_LIMIT): Flow<Response<RawConversation, GetMessagesError>>

    fun listenForNewUserMessages(owner: Email): Flow<Response<List<ConversationBrief>, GetMessagesError>>

    suspend fun sendMessage(message: Message): Response<Message, SendMessagesError>

    suspend fun updateMessage(updateMessage: UpdateMessage): Response<Unit, UpdateMessageError>

    suspend fun updateMessagesReadStatus(receiver: Email, messageIds: List<Pair<Email, MessageId>>, sendStatus: SendStatus): Response<Unit, UpdateMessageError>

    suspend fun deleteMessages(
        owner: Email,
        target: Email,
        messageIds: List<MessageId>
    ): Response<Unit, DeleteMessageError>

    suspend fun importAllUserMessages(owner: Email, rawConversation: RawConversation, conversationBrief: ConversationBrief): Response<Unit, SendMessagesError>
}