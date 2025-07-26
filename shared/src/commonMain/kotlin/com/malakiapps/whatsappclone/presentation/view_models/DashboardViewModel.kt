package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UserNotFound
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.common.getTodayLocalDate
import com.malakiapps.whatsappclone.domain.common.loggerTag1
import com.malakiapps.whatsappclone.domain.managers.AuthenticationContextManager
import com.malakiapps.whatsappclone.domain.managers.ContactsManager
import com.malakiapps.whatsappclone.domain.managers.EventsManager
import com.malakiapps.whatsappclone.domain.managers.MessagesManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.messages.ConversationBrief
import com.malakiapps.whatsappclone.domain.screens.ChatsScreenConversationRow
import com.malakiapps.whatsappclone.domain.screens.getTimeValue
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.StateValue
import com.malakiapps.whatsappclone.domain.user.addLeadingYou
import com.malakiapps.whatsappclone.domain.user.getOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val eventsManager: EventsManager,
    private val authenticationContextManager: AuthenticationContextManager,
    private val userManager: UserManager,
    private val contactsManager: ContactsManager,
    private val messagesManager: MessagesManager
) : ViewModel() {

    private val _chatsScreenConversationRow: MutableStateFlow<List<ChatsScreenConversationRow>?> =
        MutableStateFlow(null)
    val chatsScreenConversationRow: StateFlow<List<ChatsScreenConversationRow>?> =
        _chatsScreenConversationRow

    init {
        viewModelScope.launch {
        var iteration = 0
            authenticationContextManager
                .authenticationContextState
                .filter { it is StateValue }
                .flatMapLatest { authenticationContextState ->
                    messagesManager.conversationBriefs.mapNotNull { conversations ->
                        val response: MessageMapper? = authenticationContextState.getOrNull()?.let { authenticationContext ->
                            loggerTag1.i { "Context is $authenticationContext" }
                            when (conversations) {
                                is Response.Failure<List<ConversationBrief>?, GetMessagesError> -> {
                                    loggerTag1.i { "${iteration}. We failing with ${conversations.error}, on a context of $authenticationContext" }
                                    eventsManager.sendEvent(OnError(from = this@DashboardViewModel::class, error = conversations.error))
                                    _chatsScreenConversationRow.update { null }
                                    null
                                }

                                is Response.Success<List<ConversationBrief>?, GetMessagesError> -> {
                                    val currentUserProfile = userManager.userProfileState.filter { it is StateValue }.first().getOrNull()
                                        ?.let { currentValue ->
                                            currentValue.copy(
                                                name = currentValue.name.addLeadingYou(),
                                                about = About("Message yourself")
                                            )
                                        } ?: run {
                                        _chatsScreenConversationRow.update { null }
                                        eventsManager.sendEvent(OnError(from = this@DashboardViewModel::class, error = UserNotFound))
                                        return@mapNotNull null
                                    }
                                    val contactResult =
                                        contactsManager.getFriendsContacts(emails = conversations.data?.mapNotNull {
                                            //Remove the current user
                                            if (it.sender == authenticationContext.email) {
                                                null
                                            } else {
                                                it.sender
                                            }
                                        } ?: emptyList())
                                    when (contactResult) {
                                        is Response.Failure<List<Profile>, Error> -> {
                                            eventsManager.sendEvent(OnError(from = this@DashboardViewModel::class, error = contactResult.error))
                                            null
                                        }

                                        is Response.Success<List<Profile>, Error> -> {
                                            MessageMapper(
                                                contacts = contactResult.data,
                                                messages = conversations.getOrNull(),
                                                profileUser = currentUserProfile
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        loggerTag1.i { "We got a response $response" }
                        response
                    }
                }.collect { messageMapper ->
                    loggerTag1.i { "Got message mapper $messageMapper" }
                    viewModelScope.launch {
                        val contacts = messageMapper.contacts
                        val today = getTodayLocalDate()
                        val profileUser = messageMapper.profileUser
                        _chatsScreenConversationRow.update {
                            messageMapper.messages?.map { conversationBrief ->
                                val contactProfile =
                                    contacts.find { it.email == conversationBrief.sender }
                                        ?: profileUser
                                ChatsScreenConversationRow(
                                    email = conversationBrief.sender,
                                    image = contactProfile.image,
                                    name = contactProfile.name,
                                    lastMessage = conversationBrief.value,
                                    newMessagesCount = conversationBrief.newMessageCount,
                                    time = conversationBrief.time.getTimeValue(
                                        today = today
                                    ),
                                    isMyMessage = conversationBrief.sender == contactProfile.email,
                                    sendStatus = conversationBrief.sendStatus
                                )
                            }
                        }
                    }
                }
        }
    }

}

data class MessageMapper(
    val contacts: List<Profile>,
    val messages: List<ConversationBrief>?,
    val profileUser: Profile
)