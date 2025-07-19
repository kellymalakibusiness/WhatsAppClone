package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Paginate
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.messages.AnonymousUserMessageRepository
import com.malakiapps.whatsappclone.domain.messages.RawConversation
import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.MessageUpdateType
import com.malakiapps.whatsappclone.domain.messages.MessagesRepository
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first

class GetConversationUseCase(
    private val messagesRepository: MessagesRepository,
    private val anonymousUserMessageRepository: AnonymousUserMessageRepository
) {
    fun listenToNewMessages(authenticationContext: AuthenticationContext): Flow<Response<List<Pair<MessageUpdateType, Message>>, GetMessagesError>>{
        return authenticationContext.email?.let { availableEmail ->
            messagesRepository.listenForNewUserMessages(owner = availableEmail)
        } ?: run {
            emptyFlow()
        }
    }

    fun listenForConversationChanges(authenticationContext: AuthenticationContext, target: Email): Flow<Response<RawConversation, GetMessagesError>> {
        return authenticationContext.email?.let { availableEmail ->
            messagesRepository.listenForMessagesChanges(owner = availableEmail, target = target)
        } ?: run {
            anonymousUserMessageRepository.getConversation(owner = target)
        }
    }

    suspend fun getAllActiveConversations(authenticationContext: AuthenticationContext): Response<List<RawConversation>, GetMessagesError> {
        return authenticationContext.email?.let { availableEmail ->
            messagesRepository.getAllActiveConversations(owner = availableEmail)
        } ?: run {
            val selfConversation =
                anonymousUserMessageRepository.getConversation(owner = ANONYMOUS_EMAIL).first()

            when (selfConversation) {
                is Response.Failure<RawConversation, GetMessagesError> -> Response.Failure(
                    selfConversation.error
                )

                is Response.Success<RawConversation, GetMessagesError> -> {
                    Response.Success(
                        data = listOf(selfConversation.data)
                    )
                }
            }
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