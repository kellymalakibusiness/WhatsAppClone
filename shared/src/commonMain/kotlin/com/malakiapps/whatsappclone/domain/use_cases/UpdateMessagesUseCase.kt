package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.ForbiddenRequest
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UpdateMessageError
import com.malakiapps.whatsappclone.domain.messages.MessageStatusUpdate
import com.malakiapps.whatsappclone.domain.messages.MessagesRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext

class UpdateMessagesUseCase(
    private val messagesRepository: MessagesRepository
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
}