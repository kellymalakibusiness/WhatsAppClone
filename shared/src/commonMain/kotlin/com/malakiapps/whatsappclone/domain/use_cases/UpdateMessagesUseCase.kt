package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.ForbiddenRequest
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UpdateMessageError
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessagesRepository
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email

class UpdateMessagesUseCase(
    private val messagesRepository: MessagesRepository
) {

    suspend fun updateMessageSendStatus(authenticationContext: AuthenticationContext, messages: List<Pair<Email, MessageId>>, sendStatus: SendStatus): Response<Unit, UpdateMessageError> {
        return if (messages.isNotEmpty()){
            authenticationContext.email?.let { availableEmail ->
                messagesRepository.updateMessagesReadStatus(receiver = availableEmail, messageIds = messages, sendStatus = sendStatus)
            } ?: Response.Failure(ForbiddenRequest)
        } else {
            Response.Success(Unit)
        }
    }
}