package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Paginate
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.messages.AnonymousUserMessageRepository
import com.malakiapps.whatsappclone.domain.messages.ConversationBrief
import com.malakiapps.whatsappclone.domain.messages.MessagesRepository
import com.malakiapps.whatsappclone.domain.messages.RawConversation
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetConversationUseCase(
    private val messagesRepository: MessagesRepository,
    private val anonymousUserMessageRepository: AnonymousUserMessageRepository
) {
    fun listenToBriefChanges(authenticationContext: AuthenticationContext): Flow<Response<List<ConversationBrief>, GetMessagesError>>{
        return authenticationContext.email?.let { availableEmail ->
            messagesRepository.listenForNewUserMessages(owner = availableEmail)
        } ?: run {

            val conversation = anonymousUserMessageRepository.getConversation(owner = ANONYMOUS_EMAIL)
            conversation.map { response ->
                when(response){
                    is Response.Failure<RawConversation, GetMessagesError> -> Response.Failure(response.error)
                    is Response.Success<RawConversation, GetMessagesError> -> {
                        if (response.data.messages.isNotEmpty()){
                            val message = response.data.messages.first()
                            val conversationBrief = ConversationBrief(
                                target = ANONYMOUS_EMAIL,
                                newMessageCount = 0,
                                messageId = message.messageId,
                                sender = ANONYMOUS_EMAIL,
                                value = message.value,
                                sendStatus = message.attributes.sendStatus,
                                time = message.time,
                                isSelfMessage = true
                            )

                            Response.Success(listOf(conversationBrief))
                        } else {
                            Response.Success(emptyList())
                        }
                    }
                }
            }
        }
    }

    fun listenForConversationChanges(authenticationContext: AuthenticationContext, target: Email): Flow<Response<RawConversation, GetMessagesError>> {
        return authenticationContext.email?.let { availableEmail ->
            messagesRepository.listenForMessagesChanges(owner = availableEmail, target = target)
        } ?: run {
            anonymousUserMessageRepository.getConversation(owner = target)
        }
    }

    suspend fun getPaginatedConversation(authenticationContext: AuthenticationContext, target: Email, paginate: Paginate): Response<RawConversation, GetMessagesError> {
        return authenticationContext.email?.let { availableEmail ->
            messagesRepository.getConversation(owner = availableEmail, target = target, paginate = paginate)
        } ?: run {
            anonymousUserMessageRepository.getPaginatedConversation(owner = ANONYMOUS_EMAIL, paginate = paginate)
        }
    }
}