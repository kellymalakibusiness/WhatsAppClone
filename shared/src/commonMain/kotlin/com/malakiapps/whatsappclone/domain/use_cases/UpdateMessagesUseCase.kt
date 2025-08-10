package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.ForbiddenRequest
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UpdateMessageError
import com.malakiapps.whatsappclone.domain.messages.AnonymousUserMessageRepository
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageStatusUpdate
import com.malakiapps.whatsappclone.domain.messages.MessagesRepository
import com.malakiapps.whatsappclone.domain.messages.ReactToMessage
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email

class UpdateMessagesUseCase(
    private val messagesRepository: MessagesRepository,
    private val anonymousUserMessageRepository: AnonymousUserMessageRepository
) {

    suspend fun updateMessageSendStatus(authenticationContext: AuthenticationContext, messageStatusUpdate: List<MessageStatusUpdate>): Response<Unit, UpdateMessageError> {
        return if (messageStatusUpdate.isNotEmpty()){
            authenticationContext.email?.let { availableEmail ->
                messagesRepository.updateMessagesReadStatus(receiver = availableEmail, messageStatusUpdate = messageStatusUpdate)
            } ?: Response.Failure(ForbiddenRequest)
        } else {
            Response.Success(Unit)
        }
    }

    suspend fun updateMessageReaction(authenticationContext: AuthenticationContext, messageId: MessageId, reaction: String, isSender: Boolean, targetEmail: Email): Response<Unit, UpdateMessageError> {
        return authenticationContext.email?.let { availableEmail ->
            val sender = if (isSender) { availableEmail } else { targetEmail }
            val receiver = if (isSender) { targetEmail } else { availableEmail }
            messagesRepository.updateMessage(
                ReactToMessage(
                    sender = sender,
                    receiver = receiver,
                    messageId = messageId,
                    addReaction = reaction,
                    isSender = isSender
                )
            )
        } ?: run {
            anonymousUserMessageRepository.updateMessage(
                ReactToMessage(
                    sender = ANONYMOUS_EMAIL,
                    receiver = ANONYMOUS_EMAIL,
                    messageId = messageId,
                    addReaction = reaction,
                    isSender = isSender
                )
            )
        }
    }
}