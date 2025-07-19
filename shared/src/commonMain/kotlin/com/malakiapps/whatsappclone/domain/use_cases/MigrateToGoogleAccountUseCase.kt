package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.CreateUserError
import com.malakiapps.whatsappclone.domain.common.EmailNotFound
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.messages.AnonymousUserMessageRepository
import com.malakiapps.whatsappclone.domain.messages.MessagesRepository
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AnonymousUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticatedUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.None
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.Some
import com.malakiapps.whatsappclone.domain.user.SignInResponse
import com.malakiapps.whatsappclone.domain.user.UserContactUpdate


class MigrateToGoogleAccountUseCase(
    private val anonymousUserAccountRepository: AnonymousUserAccountRepository,
    private val userAccountRepository: AuthenticatedUserAccountRepository,
    private val anonymousUserMessageRepository: AnonymousUserMessageRepository,
    private val userMessagesRepository: MessagesRepository,
) {
    suspend operator fun invoke(signInResponse: SignInResponse): Response<Profile, CreateUserError>{
        return signInResponse.authenticationContext.email?.let { availableEmail ->
            //MIGRATE THE MESSAGES
            //Get the existing messages
            anonymousUserMessageRepository.exportAllUserMessages(owner = availableEmail).getOrNull()?.let { existingMessages ->
                //Add them to the new account
                val result = userMessagesRepository.importAllUserMessages(owner = availableEmail, rawConversation = existingMessages)

                result.getOrNull()?.let {
                    //DELETE THE ANONYMOUS USER MESSAGES
                    anonymousUserAccountRepository.deleteAccount(email = ANONYMOUS_EMAIL)
                    anonymousUserMessageRepository.deleteMessages(owner = ANONYMOUS_EMAIL, messageIds = existingMessages.messages.map { it.messageId })
                }
            }

            //UPDATE THE NEW ACCOUNT WITH THE ANONYMOUS SETTINGS
            val anonymousUserAccount = anonymousUserAccountRepository.getContact(ANONYMOUS_EMAIL).getOrNull()
            userAccountRepository.upgradeContactFromAnonymous(
                userContactUpdate = UserContactUpdate(
                    email = availableEmail,
                    name = anonymousUserAccount?.name?.let { Some(it) } ?: None,
                    about = anonymousUserAccount?.about?.let { Some(it) } ?: None,
                    image = (anonymousUserAccount?.image ?: signInResponse.initialBase64ProfileImage)?.let { Some(it) } ?: None
                )
            )
        } ?: Response.Failure(EmailNotFound)
    }
}