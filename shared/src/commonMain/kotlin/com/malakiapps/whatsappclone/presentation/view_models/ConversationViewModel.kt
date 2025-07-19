package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.Event
import com.malakiapps.whatsappclone.domain.common.PlayMessageTone
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.managers.ContactsManager
import com.malakiapps.whatsappclone.domain.managers.MessagesManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.screens.ConversationMessage
import com.malakiapps.whatsappclone.domain.screens.MessageType
import com.malakiapps.whatsappclone.domain.screens.TimeCard
import com.malakiapps.whatsappclone.domain.screens.getDayValue
import com.malakiapps.whatsappclone.domain.screens.getMessageType
import com.malakiapps.whatsappclone.domain.screens.getTimeValue
import com.malakiapps.whatsappclone.domain.use_cases.SendMessageUseCase
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.Some
import com.malakiapps.whatsappclone.domain.user.TimeValue
import com.malakiapps.whatsappclone.domain.user.getOrNull
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

class ConversationViewModel(
    private val userManager: UserManager,
    private val messagesManager: MessagesManager,
    private val contactsManager: ContactsManager,
) : ViewModel() {
    private val _eventChannel = Channel<Event>()
    val eventsChannelFlow = _eventChannel.receiveAsFlow()

    private var targetEmail: Email? = null
    private val _targetContact: MutableStateFlow<Profile?> = MutableStateFlow(null)
    val targetContact: StateFlow<Profile?> = _targetContact

    @OptIn(ExperimentalTime::class)
    val conversation = messagesManager.conversations.map { value ->
        targetEmail?.let { availableEmail ->
            value.getOrNull()?.find { it.contact2 == availableEmail }
                ?.let { conversationWithMessageContext ->
                    val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    var thereIsANewMessage = false
                    var currentBufferType = conversationWithMessageContext.messages.firstOrNull()?.getMessageType(targetEmail) ?: MessageType.None
                    var currentTimeValue = conversationWithMessageContext.messages.firstOrNull()?.time?.getDayValue(today = today) ?: TimeValue("Today")
                    val messageBuffer = mutableListOf<Message>()
                    val conversationMessages = buildList {
                        conversationWithMessageContext.messages.forEach { message ->
                            val messageTimeValue = message.time.getDayValue(today = today)
                            val messageType = message.getMessageType(target = targetEmail)
                            if(messageTimeValue != currentTimeValue){
                                addAll(
                                    messageBuffer.mapIndexed { index, messageOnBuffer ->
                                        val isStartOfReply = index == messageBuffer.size -1
                                        ConversationMessage(
                                            messageId = messageOnBuffer.messageId,
                                            message = messageOnBuffer.value,
                                            time = TimeValue("${messageOnBuffer.time.time.hour}:${messageOnBuffer.time.time.minute}"),
                                            sendStatus = messageOnBuffer.attributes.sendStatus,
                                            messageType = currentBufferType,
                                            previousMessageType = if(index == 0) MessageType.None else currentBufferType,//if(isStartOfReply) MessageType.None else currentBufferType,
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
                                if(currentBufferType != messageType){
                                    //We experienced a message type change
                                    addAll(
                                        messageBuffer.mapIndexed { index, messageOnBuffer ->
                                            val isStartOfReply = index == messageBuffer.size -1
                                            ConversationMessage(
                                                messageId = messageOnBuffer.messageId,
                                                message = messageOnBuffer.value,
                                                time = TimeValue("${messageOnBuffer.time.time.hour}:${messageOnBuffer.time.time.minute}"),
                                                sendStatus = messageOnBuffer.attributes.sendStatus,
                                                messageType = currentBufferType,
                                                previousMessageType = if(index == 0) MessageType.None else currentBufferType,
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
                            if(message.sender == targetEmail && message.attributes.sendStatus == SendStatus.ONE_TICK){
                                thereIsANewMessage = true
                            }
                        }
                        if(messageBuffer.isNotEmpty()){
                            addAll(
                                messageBuffer.mapIndexed { index, messageOnBuffer ->
                                    val isStartOfReply = index == messageBuffer.size -1
                                    ConversationMessage(
                                        messageId = messageOnBuffer.messageId,
                                        message = messageOnBuffer.value,
                                        time = TimeValue("${messageOnBuffer.time.time.hour}:${messageOnBuffer.time.time.minute}"),
                                        sendStatus = messageOnBuffer.attributes.sendStatus,
                                        messageType = currentBufferType,
                                        previousMessageType = if(index == 0) MessageType.None else currentBufferType,
                                        isStartOfReply = isStartOfReply
                                    )
                                }
                            )
                            val day = messageBuffer.first().time.getDayValue(today = today)
                            if(currentTimeValue == day){
                                add(TimeCard(time = currentTimeValue))
                            }
                        }
                    }
                    if (thereIsANewMessage){
                        _eventChannel.send(PlayMessageTone)
                    }
                    conversationMessages
                }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun setTargetEmail(email: Email) {
        viewModelScope.launch {
            targetEmail = email
            messagesManager.changeCurrentOnConversation(email)
            val authenticatedUserEmail = userManager.userProfileState.value.getOrNull()?.email
            if(authenticatedUserEmail != email){
                //Check and add the contact
                val userDetails = userManager.userDetailsState.value.getOrNull()
                if (userDetails?.contacts?.contains(email) != true) {
                    userManager.updateUserDetails(
                        addContactUpdate = Some(email)
                    )
                }

                //Listen for contact changes
                contactsManager
                    .listenToContactChanges(email = email)
                    .collect {
                        it.getOrNull()?.let { contactChange ->
                            _targetContact.update {
                                contactChange
                            }
                        }
                    }
            } else {
                _targetContact.update {
                    userManager.userProfileState.value.getOrNull()
                }
            }
            messagesManager.listenForConversationChanges(email)
        }
    }

    fun sendMessage(messageValue: String){
        messagesManager.sendMessage(messageValue)
    }

    override fun onCleared() {
        super.onCleared()
        messagesManager.changeCurrentOnConversation(null)
    }

}