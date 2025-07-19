package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.messages.AnonymousUserMessageRepository
import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.MessagesRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext

class SendMessageUseCase(
    private val messagesRepository: MessagesRepository,
    private val anonymousUserMessageRepository: AnonymousUserMessageRepository
) {
    suspend operator fun invoke(authenticationContext: AuthenticationContext, message: Message){
        authenticationContext.email?.let { availableEmail ->
            messagesRepository.sendMessage(message = message)
        } ?: run {
            anonymousUserMessageRepository.sendMessage(message = message)
        }
    }
}