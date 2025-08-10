package com.malakiapps.whatsappclone.domain.managers

import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.common.getTodayLocalDateTime
import com.malakiapps.whatsappclone.domain.messages.ConversationBrief
import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.MessageAttributes
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageStatusUpdate
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.use_cases.GetConversationUseCase
import com.malakiapps.whatsappclone.domain.use_cases.SendMessageUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateMessagesUseCase
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.StateValue
import com.malakiapps.whatsappclone.domain.user.getOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MessagesManager(
    authenticationContextManager: AuthenticationContextManager,
    private val getConversationUseCase: GetConversationUseCase,
    private val updateMessagesUseCase: UpdateMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var currentOnConversation: Email? = null
    private val _authenticationContextState =
        authenticationContextManager.authenticationContextState

    private val _conversationBriefs: MutableStateFlow<Response<List<ConversationBrief>?, GetMessagesError>> = MutableStateFlow(Response.Success(null))
    val conversationBriefs: StateFlow<Response<List<ConversationBrief>?, GetMessagesError>> = _conversationBriefs

    private val _notificationChannel = Channel<ConversationBrief>()
    private val _tonePlayerChannel = Channel<Boolean>()

    init {
        scope.launch {
            _authenticationContextState
                .filter { it is StateValue }
                .collect { currentValue ->
                    currentValue.getOrNull()?.let { availableAuth ->
                        //1.Start listening for conversation brief changes using a flow
                        listenForNewMessagesAndUpdateBrief() //This also pushes notifications to the notification channel
                    } ?: run {
                        _conversationBriefs.update { Response.Success(null) }
                    }
                }
        }
    }

    fun listenForNotifications(): Flow<ConversationBrief> {
        return _notificationChannel.receiveAsFlow()
    }

    fun listenForTonePlayer(): Flow<Boolean> {
        return _tonePlayerChannel.receiveAsFlow()
    }

    private fun listenForNewMessagesAndUpdateBrief() {
        scope.launch {
            getAuthenticationContext()?.let { authenticationContext ->
            getConversationUseCase.listenToBriefChanges(authenticationContext = authenticationContext).collect { newValue ->
                _conversationBriefs.update {
                    newValue
                }

                newValue.getOrNull()?.let { value ->
                    //Check for notifications
                    val oneTickUpdates = mutableListOf<MessageStatusUpdate>()
                    value.forEach { brief ->
                        //Check that its not me and it has one tick
                        if(authenticationContext.email != brief.sender && brief.sendStatus == SendStatus.ONE_TICK){
                            //If we're not in this conversation, send notification
                            val sendStatus = if(brief.target != currentOnConversation){
                                _notificationChannel.trySend(brief)
                                SendStatus.TWO_TICKS //Not in convo, heard the text but not open yet
                            } else {
                                //If we are in this convo, send tone
                                _tonePlayerChannel.trySend(true)
                                SendStatus.TWO_TICKS_READ //The message should be marked as read
                            }
                            oneTickUpdates.add(
                                MessageStatusUpdate(
                                    target = brief.target,
                                    messageId = brief.messageId,
                                    sendStatus = sendStatus,
                                    hasNotificationCounter = sendStatus == SendStatus.TWO_TICKS
                                )
                            )
                        }
                    }

                    //Make update for readStatus
                    if (oneTickUpdates.isNotEmpty()){
                        //This one is mainly to let the secondary user know we have received their message
                        updateMessagesUseCase.updateMessageSendStatus(authenticationContext = authenticationContext, messageStatusUpdate = oneTickUpdates)
                    }


                }
            }
            }
        }
    }

    fun sendMessage(messageValue: String){
        scope.launch {
            getAuthenticationContext()?.let { authenticationContext ->
                val message = Message(
                    sender = authenticationContext.email ?: ANONYMOUS_EMAIL,
                    receiver = currentOnConversation ?: ANONYMOUS_EMAIL,
                    time = getTodayLocalDateTime(),
                    value = MessageValue(messageValue),
                    attributes = MessageAttributes(
                        updated = false,
                        sendStatus = if(currentOnConversation == authenticationContext.email) SendStatus.TWO_TICKS_READ else SendStatus.ONE_TICK,
                        isDeleted = false,
                        senderReaction = null,
                        receiverReaction = null
                    )
                )
                sendMessageUseCase.invoke(authenticationContext, message)
            }
        }
    }

    fun sendReaction(messageId: MessageId, reaction: String, isSender: Boolean, targetEmail: Email){
        scope.launch {
            getAuthenticationContext()?.let { authenticationContext ->
                updateMessagesUseCase.updateMessageReaction(authenticationContext, messageId, reaction, isSender, targetEmail)
            }
        }
    }

    fun changeCurrentOnConversation(on: Email?) {
        currentOnConversation = on
    }

    private fun getAuthenticationContext(): AuthenticationContext? {
        return _authenticationContextState.value.getOrNull()
    }
}