package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.common.getTodayLocalDate
import com.malakiapps.whatsappclone.domain.managers.AuthenticationContextManager
import com.malakiapps.whatsappclone.domain.managers.ContactsManager
import com.malakiapps.whatsappclone.domain.managers.EventsManager
import com.malakiapps.whatsappclone.domain.managers.MessagesManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.RawConversation
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.screens.ConversationMessage
import com.malakiapps.whatsappclone.domain.screens.MessageCard
import com.malakiapps.whatsappclone.domain.screens.MessageType
import com.malakiapps.whatsappclone.domain.screens.TimeCard
import com.malakiapps.whatsappclone.domain.screens.getDayValue
import com.malakiapps.whatsappclone.domain.screens.getMessageType
import com.malakiapps.whatsappclone.domain.use_cases.GetConversationUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateMessagesUseCase
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.Some
import com.malakiapps.whatsappclone.domain.user.TimeValue
import com.malakiapps.whatsappclone.domain.user.TimeValue.Companion.toParsedTimeValue
import com.malakiapps.whatsappclone.domain.user.addLeadingYou
import com.malakiapps.whatsappclone.domain.user.getOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val eventsManager: EventsManager,
    private val authenticationContextManager: AuthenticationContextManager,
    private val userManager: UserManager,
    private val messagesManager: MessagesManager,
    private val contactsManager: ContactsManager,
    private val getConversationUseCase: GetConversationUseCase,
    private val updateMessagesUseCase: UpdateMessagesUseCase,
    private val targetEmail: Email
) : ViewModel() {
    private val _targetContact: MutableStateFlow<Profile?> = MutableStateFlow(null)
    val targetContact: StateFlow<Profile?> = _targetContact
    var isSelfChat = false

    init {
        viewModelScope.launch {
            //1. Change current on conversation to prevent The manager from sending notifications
            messagesManager.changeCurrentOnConversation(targetEmail)
            val authenticatedUserEmail = authenticationContextManager.authenticationContextState.value.getOrNull()?.email

            //2. Check if its not messaging ourself, so that we can add the contact if they dont exist as a contact
            if (authenticatedUserEmail != null && authenticatedUserEmail != targetEmail) {
                //Check and add the contact
                val userDetails = userManager.userDetailsState.value.getOrNull()
                if (userDetails?.contacts?.contains(targetEmail) != true) {
                    //Add contact
                    userManager.updateUserDetails(
                        addContactUpdate = Some(targetEmail)
                    )
                }

                //3. Listen for contact changes so that we get their current state while on their conversation
                listenForContactChanges()
            } else {
                //3(b). If its a self message, just set our profile as the target contact
                _targetContact.update {
                    userManager.userProfileState.value.getOrNull()?.let { currentValue ->
                        currentValue.copy(
                            name = currentValue.name.addLeadingYou(),
                            about = About("Message yourself")
                        )
                    }
                }
                isSelfChat = true
            }

            //4. Listen for message changes
            listenToConversationChangesAndUpdateConversation()
        }
    }
    private val _conversation: MutableStateFlow<List<MessageCard>?> = MutableStateFlow(null)

    val conversation: StateFlow<List<MessageCard>?> = _conversation

    fun sendMessage(messageValue: String) {
        messagesManager.sendMessage(messageValue)
    }

    private fun convertRawConversationToMessageCards(input: RawConversation): List<MessageCard>? {
        val today = getTodayLocalDate()
        var currentBufferType = input.messages.firstOrNull()?.getMessageType(targetEmail) ?: MessageType.None
        var currentTimeValue = input.messages.firstOrNull()?.time?.getDayValue(today = today) ?: TimeValue("Today")
        val messageBuffer = mutableListOf<Message>()

        return buildList {
            input.messages.forEach { message ->
                val messageTimeValue = message.time.getDayValue(today = today)
                val messageType = message.getMessageType(target = targetEmail)
                if (messageTimeValue != currentTimeValue) {
                    addAll(
                        messageBuffer.mapIndexed { index, messageOnBuffer ->
                            val isStartOfReply = index == messageBuffer.size -1
                            ConversationMessage(
                                messageId = messageOnBuffer.messageId,
                                message = messageOnBuffer.value,
                                time = messageOnBuffer.time.time.toParsedTimeValue(),
                                sendStatus = messageOnBuffer.attributes.sendStatus,
                                messageType = currentBufferType,
                                previousMessageType = if (index == 0) MessageType.None else currentBufferType,//if(isStartOfReply) MessageType.None else currentBufferType,
                                isStartOfReply = isStartOfReply
                            )
                        }
                    )
                    add(TimeCard(time = currentTimeValue))
                    messageBuffer.clear()
                    currentTimeValue = messageTimeValue
                    currentBufferType = messageType
                    messageBuffer.add(message)
                } else {
                    //Didn't get caught by the date change
                    //Check for message type change
                    if (currentBufferType != messageType) {
                        //We experienced a message type change
                        addAll(
                            messageBuffer.mapIndexed { index, messageOnBuffer ->
                                val isStartOfReply = index == messageBuffer.size -1
                                ConversationMessage(
                                    messageId = messageOnBuffer.messageId,
                                    message = messageOnBuffer.value,
                                    time = messageOnBuffer.time.time.toParsedTimeValue(),
                                    sendStatus = messageOnBuffer.attributes.sendStatus,
                                    messageType = currentBufferType,
                                    previousMessageType = if (index == 0) MessageType.None else currentBufferType,
                                    isStartOfReply = isStartOfReply
                                )
                            }
                        )
                        messageBuffer.clear()
                        currentBufferType = messageType
                        messageBuffer.add(message)
                    } else {
                        messageBuffer.add(message)
                    }
                }
            }

            if (messageBuffer.isNotEmpty()) {
                addAll(
                    messageBuffer.mapIndexed { index, messageOnBuffer ->
                        val isStartOfReply = index == messageBuffer.size -1
                        ConversationMessage(
                            messageId = messageOnBuffer.messageId,
                            message = messageOnBuffer.value,
                            time = messageOnBuffer.time.time.toParsedTimeValue(),
                            sendStatus = messageOnBuffer.attributes.sendStatus,
                            messageType = currentBufferType,
                            previousMessageType = if (index == 0) MessageType.None else currentBufferType,
                            isStartOfReply = isStartOfReply
                        )
                    }
                )
                val day = messageBuffer.first().time.getDayValue(today = today)
                if (currentTimeValue == day) {
                    add(TimeCard(time = currentTimeValue))
                }
            }
        }
    }

    private fun listenToConversationChangesAndUpdateConversation() {
        viewModelScope.launch {
            authenticationContextManager.authenticationContextState.value.getOrNull()?.let { authenticationContext ->
                getConversationUseCase
                    .listenForConversationChanges(
                        authenticationContext = authenticationContext,
                        target = targetEmail
                    ).collect { rawConversation ->
                        when(rawConversation){
                            is Response.Failure<RawConversation, GetMessagesError> -> {
                                eventsManager.sendEvent(OnError(from = this@ConversationViewModel::class, error = rawConversation.error))
                            }
                            is Response.Success<RawConversation, GetMessagesError> -> {

                                //Checking if our own messages were sent successfully
                                val updatedMessages = if(rawConversation.data.hasPendingWrites){
                                    var foundAllPendingWrites = false
                                    rawConversation.data.messages.map { message ->
                                        if(!foundAllPendingWrites && message.sender == authenticationContext.email && message.attributes.sendStatus == SendStatus.ONE_TICK){
                                            message.copy(attributes = message.attributes.copy(sendStatus = SendStatus.LOADING))
                                        } else {
                                            foundAllPendingWrites = true
                                            message
                                        }
                                    }
                                } else {
                                    rawConversation.data.messages
                                }

                                _conversation.update {
                                    convertRawConversationToMessageCards(input = rawConversation.data.copy(messages = updatedMessages))
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun listenForContactChanges() {
        viewModelScope.launch {
            contactsManager
                .listenToContactChanges(email = targetEmail)
                .collect {
                    it.getOrNull()?.let { contactChange ->
                        _targetContact.update {
                            contactChange
                        }
                        contactsManager.updateContactsState(listOf(contactChange))
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        messagesManager.changeCurrentOnConversation(null)
    }

}