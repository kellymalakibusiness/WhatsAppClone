package com.malakiapps.whatsappclone.data

import com.malakiapps.whatsappclone.data.room.MessageDao
import com.malakiapps.whatsappclone.data.room.toMessage
import com.malakiapps.whatsappclone.data.room.toMessageEntity
import com.malakiapps.whatsappclone.domain.common.DeleteMessageError
import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Paginate
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.SendMessagesError
import com.malakiapps.whatsappclone.domain.common.UpdateMessageError
import com.malakiapps.whatsappclone.domain.messages.AnonymousUserMessageRepository
import com.malakiapps.whatsappclone.domain.messages.ChangeMessageBody
import com.malakiapps.whatsappclone.domain.messages.RawConversation
import com.malakiapps.whatsappclone.domain.messages.DeleteMessageForBoth
import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.ReactToMessage
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.messages.UpdateMessage
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnonymousLocalUserMessageRepository(
    private val messageDao: MessageDao
): AnonymousUserMessageRepository {
    override fun getConversation(owner: Email): Flow<Response<RawConversation, GetMessagesError>> {
        return messageDao.getConversation().map { messages ->
            val messages = messages.map { it.toMessage() }

            Response.Success(
                RawConversation(
                    contact1 = owner,
                    contact2 = owner,
                    messages = messages
                )
            )
        }
    }

    override suspend fun getPaginatedConversation(owner: Email, paginate: Paginate): Response<RawConversation, GetMessagesError>{
        return messageDao.getMessagesFrom(lastMessageId = paginate.fromFieldValue.toString().toInt()).let { messageEntities ->
            val messages = messageEntities.map { it.toMessage() }
            Response.Success(
                RawConversation(
                    contact1 = owner,
                    contact2 = owner,
                    messages = messages
                )
            )
        }
    }

    override suspend fun sendMessage(message: Message): Response<Message, SendMessagesError> {
        messageDao.sendMessage(message = message.toMessageEntity().copy(sendStatus = SendStatus.TWO_TICKS_READ.name))

        return Response.Success(message)
    }

    override suspend fun updateMessage(updateMessage: UpdateMessage): Response<Unit, UpdateMessageError> {
        when(updateMessage){
            is ChangeMessageBody -> {
                messageDao.changeMessageBody(
                    messageId = updateMessage.messageId.value.toInt(),
                    newValue = updateMessage.updatedValue
                )
            }
            is DeleteMessageForBoth -> {
                messageDao.deleteMessages(messageIds = listOf(updateMessage.messageId.value.toInt()))
            }
            is ReactToMessage -> {
                messageDao.reactToMessage(messageId = updateMessage.messageId.value.toInt(), reaction = updateMessage.addReaction)
            }
        }

        return Response.Success(Unit)
    }

    override suspend fun deleteMessages(owner: Email, messageIds: List<MessageId>): Response<Unit, DeleteMessageError> {
        messageDao.deleteMessages(messageIds = messageIds.map { it.value.toInt() })

        return Response.Success(Unit)
    }

    override suspend fun exportAllUserMessages(owner: Email): Response<RawConversation, GetMessagesError> {
        val result = messageDao.exportUserMessages().map { it.toMessage() }

        return Response.Success(
            RawConversation(
                messages = result,
                contact1 = ANONYMOUS_EMAIL,
                contact2 = ANONYMOUS_EMAIL
            )
        )
    }
}