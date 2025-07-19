package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Event
import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.managers.ContactsManager
import com.malakiapps.whatsappclone.domain.managers.MessagesManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.messages.ConversationWithMessageContext
import com.malakiapps.whatsappclone.domain.screens.ChatsScreenConversationRow
import com.malakiapps.whatsappclone.domain.screens.toConversationRowObject
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.getOrNull
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val userManager: UserManager,
    private val contactsManager: ContactsManager,
    private val messagesManager: MessagesManager
) : ViewModel() {
    private val _eventChannel = Channel<Event>()
    val eventsChannelFlow = _eventChannel.receiveAsFlow()

    private val _chatsScreenConversationRow: MutableStateFlow<List<ChatsScreenConversationRow>?> = MutableStateFlow(null)
    val chatsScreenConversationRow: StateFlow<List<ChatsScreenConversationRow>?> = _chatsScreenConversationRow

    init {
        viewModelScope.launch {
            messagesManager
                .conversations
                .collect { conversations ->
                    when(conversations){
                        is Response.Failure<List<ConversationWithMessageContext>?, GetMessagesError> -> {
                            _eventChannel.send(OnError(conversations.error))
                            _chatsScreenConversationRow.update { null }
                        }
                        is Response.Success<List<ConversationWithMessageContext>?, GetMessagesError> -> {
                            val contactResult = contactsManager.getFriendsContacts(emails = conversations.data?.mapNotNull {
                                //Remove the current user
                                if(it.contact2 == it.contact1){
                                    null
                                } else {
                                    it.contact2
                                }
                            } ?: emptyList())
                            when(contactResult){
                                is Response.Failure<List<Profile>, Error> -> {
                                    _eventChannel.send(OnError(contactResult.error))
                                }
                                is Response.Success<List<Profile>, Error> -> {
                                    val contacts = contactResult.data
                                    _chatsScreenConversationRow.update {
                                        conversations.data?.mapNotNull { conversation ->
                                            if(conversation.messages.isNotEmpty()){
                                                val contactProfile = contacts.find { it.email == conversation.contact2 }
                                                Pair(contactProfile ?: userManager.userProfileState.value.getOrNull(), conversation).toConversationRowObject()
                                            } else {
                                                null
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

}