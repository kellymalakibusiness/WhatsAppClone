package com.malakiapps.whatsappclone.domain.managers

import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.messages.ConversationWithMessageContext
import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.MessageAttributes
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.RawConversation
import com.malakiapps.whatsappclone.domain.messages.MessageUpdateType
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.messages.isMessageRead
import com.malakiapps.whatsappclone.domain.messages.isMessageReceived
import com.malakiapps.whatsappclone.domain.messages.toConversationWithContext
import com.malakiapps.whatsappclone.domain.messages.toRawConversation
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
    private val _conversations: MutableStateFlow<Response<List<ConversationWithMessageContext>?, GetMessagesError>> =
        MutableStateFlow(Response.Success(null))
    val conversations: StateFlow<Response<List<ConversationWithMessageContext>?, GetMessagesError>> = _conversations
        .map {
            val value: Response<List<ConversationWithMessageContext>?, GetMessagesError> = when (it) {
                is Response.Failure<List<ConversationWithMessageContext>?, GetMessagesError> -> Response.Failure(
                    it.error
                )

                is Response.Success<List<ConversationWithMessageContext>?, GetMessagesError> -> {
                    Response.Success(it.data?.sortedByDescending { it.messages.first().time })
                }
            }
            value
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Lazily,
            initialValue = Response.Success(null)
        )

    init {
        scope.launch {
            _authenticationContextState
                .filter { it is StateValue }
                .collect { currentValue ->
                    currentValue.getOrNull()?.let { availableAuth ->
                        //First lets get all the active conversations
                        val response =
                            getConversationUseCase.getAllActiveConversations(availableAuth)
                        val conversationWithMessageContext =
                            response.toConversationWithContextResponse(
                                owner = availableAuth.email ?: ANONYMOUS_EMAIL
                            )
                        _conversations.update { conversationWithMessageContext }


                        //Get messages from sender with just one tick and send them to notification
                        conversationWithMessageContext.getOrNull()?.let { conversationsMap ->
                            val unReadMessages =
                                conversationsMap.mapNotNull { conversation ->
                                    if (conversation.noOfUnreadMessages > 0) {
                                        val messages = buildList {
                                            conversation.messages.forEach { message ->
                                                if (!message.isMessageRead(
                                                        availableAuth.email ?: ANONYMOUS_EMAIL
                                                    )
                                                ) {
                                                    add(message)
                                                }
                                            }
                                        }
                                        conversation.copy(messages = messages)
                                    } else {
                                        null
                                    }
                                }

                            showNotifications(messages = unReadMessages)

                            //Now get all unread messages and filter them more to get the 1 tick ones for update
                            val oneTickMessages = unReadMessages.map { conversation ->
                                conversation.messages.mapNotNull { message ->
                                    if (!message.isMessageReceived(
                                            availableAuth.email ?: ANONYMOUS_EMAIL
                                        )
                                    ) {
                                        Pair(conversation.contact2, message.messageId)
                                    } else {
                                        null
                                    }
                                }
                            }.flatten()

                            updateMessageReadStatus(
                                authenticationContext = availableAuth,
                                newStatus = SendStatus.TWO_TICKS,
                                messages = oneTickMessages
                            )
                        }

                        //Begin the check for the new messages
                        listenForNewMessages(authenticationContext = availableAuth)
                    } ?: run {
                        _conversations.update { Response.Success(null) }
                    }
                }
        }
    }

    suspend fun listenForConversationChanges(target: Email) {
        getAuthenticationContext()?.let { authenticationContext ->
            getConversationUseCase
                .listenForConversationChanges(
                    authenticationContext = authenticationContext,
                    target = target
                )
                .collect { newChange ->
                    newChange.getOrNull()?.let { conversationUpdates ->
                        _conversations.value.getOrNull()?.let { currentConversationList ->
                            val currentConversation =
                                currentConversationList
                                    .find { it.contact2 == target }
                                    ?.toRawConversation()
                                    ?: RawConversation(
                                        contact1 = authenticationContext.email ?: ANONYMOUS_EMAIL,
                                        contact2 = target,
                                        messages = emptyList()
                                    )
                            val oldUpdatedMessages =
                                conversationUpdates.messages.mapNotNull { updatedMessage ->
                                    currentConversation.messages.find { it.messageId == updatedMessage.messageId }
                                }
                            val updatedMessages = currentConversation.messages.map { eachOldMessage ->
                                    if (oldUpdatedMessages.contains(eachOldMessage)) {
                                        val newMessage =
                                            conversationUpdates.messages.find { eachOldMessage.messageId == it.messageId }
                                                ?: eachOldMessage
                                        newMessage
                                    } else {
                                        eachOldMessage
                                    }
                                }

                            val conversation = currentConversation.copy(messages = updatedMessages)

                            //Update our conversation
                            _conversations.update {
                                Response.Success(
                                    currentConversationList.map {
                                        if(it.contact2 != target){
                                            it
                                        } else {
                                            conversation.toConversationWithContext(owner = authenticationContext.email ?: ANONYMOUS_EMAIL)
                                        }
                                    }
                                )
                            }

                            //Update all messages that are not read to Read
                            val unreadMessages = conversation.messages.mapNotNull { eachMessage ->
                                if (!eachMessage.isMessageRead(
                                        owner = authenticationContext.email ?: ANONYMOUS_EMAIL
                                    )
                                ) {
                                    Pair(conversation.contact2, eachMessage.messageId)
                                } else {
                                    null
                                }
                            }

                            //TODO("This should be also affected by the user's settings")
                            updateMessageReadStatus(
                                authenticationContext = authenticationContext,
                                newStatus = SendStatus.TWO_TICKS_READ,
                                messages = unreadMessages
                            )
                        }
                    }
                }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun sendMessage(messageValue: String){
        scope.launch {
            getAuthenticationContext()?.let { authenticationContext ->
                val message = Message(
                    sender = authenticationContext.email ?: ANONYMOUS_EMAIL,
                    receiver = currentOnConversation ?: ANONYMOUS_EMAIL,
                    time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    value = MessageValue(messageValue),
                    attributes = MessageAttributes(
                        updated = false,
                        sendStatus = SendStatus.LOADING,
                        isDeleted = false,
                        senderReaction = null,
                        receiverReaction = null
                    )
                )
                sendMessageUseCase.invoke(authenticationContext, message)
            }
        }
    }

    private suspend fun listenForNewMessages(authenticationContext: AuthenticationContext) {
        getConversationUseCase
            .listenToNewMessages(authenticationContext = authenticationContext)
            .collect { update ->
                update.getOrNull()?.let { updateMessages ->
                    val updatesToTwoTicks = mutableListOf<Pair<Email, MessageId>>()
                    val notificationMessages = mutableListOf<Message>()
                    updateMessages.forEach {
                        val message = it.second
                        when (it.first) {
                            MessageUpdateType.NEW_MESSAGE -> {
                                if (message.sender != authenticationContext.email && message.sender != currentOnConversation) {
                                    if (message.attributes.sendStatus == SendStatus.ONE_TICK) {
                                        updatesToTwoTicks.add(
                                            Pair(
                                                message.sender,
                                                message.messageId
                                            )
                                        )
                                    }

                                    if (message.attributes.sendStatus == SendStatus.TWO_TICKS || message.attributes.sendStatus == SendStatus.ONE_TICK) {
                                        notificationMessages.add(message)
                                    }
                                }
                            }

                            MessageUpdateType.UPDATED_MESSAGE -> Unit
                            MessageUpdateType.DELETED_MESSAGE -> Unit
                        }

                        //Now update our conversations with the new messages
                        if (message.sender != currentOnConversation && message.receiver != currentOnConversation) {
                            _conversations.value.getOrNull()?.let { availableConversation ->
                                val currentConversation = availableConversation.find { conversation -> conversation.contact2 == message.sender }
                                val updatedMessages = buildList {
                                    currentConversation?.messages?.let { messages ->
                                        addAll(messages)
                                        val oldMessage =
                                            messages.find { it.messageId == message.messageId }
                                        if (oldMessage != null) {
                                            val oldMessageIndex = messages.indexOf(oldMessage)
                                            add(oldMessageIndex, message)
                                        } else {
                                            add(message)
                                        }
                                    } ?: run {
                                        add(message)
                                    }
                                }
                                val conversation = currentConversation?.copy(messages = updatedMessages)?.toRawConversation()
                                    ?: run {
                                            RawConversation(
                                                contact1 = message.receiver,
                                                contact2 = message.sender,
                                                messages = updatedMessages
                                            )
                                        }

                                _conversations.update {
                                    Response.Success(
                                        availableConversation.map {
                                            if(it.contact2 != message.sender){
                                                it
                                            } else {
                                                conversation.toConversationWithContext(owner = authenticationContext.email ?: ANONYMOUS_EMAIL)
                                            }
                                        }
                                    )
                                }
                            }
                        }

                    }

                    updateMessageReadStatus(
                        authenticationContext = authenticationContext,
                        newStatus = SendStatus.TWO_TICKS,
                        messages = updatesToTwoTicks
                    )

                    val messagesByConversations = buildMap<Email, List<Message>> {
                        notificationMessages.forEach { message ->
                            val currentMessages = this[message.sender]
                            val updatedMessages = currentMessages?.let { availableCurrentMessages ->
                                buildList {
                                    addAll(availableCurrentMessages)
                                    add(message)
                                }
                            } ?: listOf(message)
                            put(message.sender, updatedMessages)
                        }
                    }

                    if (messagesByConversations.isNotEmpty()) {
                        val conversations = messagesByConversations.map { (key, value) ->
                            ConversationWithMessageContext(
                                contact1 = authenticationContext.email ?: ANONYMOUS_EMAIL,
                                contact2 = key,
                                messages = value,
                                noOfUnreadMessages = value.size,
                                time = value.first().time
                            )
                        }

                        showNotifications(messages = conversations)
                    }
                }
            }
    }

    fun changeCurrentOnConversation(on: Email?) {
        currentOnConversation = on
    }

    private suspend fun updateMessageReadStatus(
        authenticationContext: AuthenticationContext,
        newStatus: SendStatus,
        messages: List<Pair<Email, MessageId>>
    ) {
        updateMessagesUseCase.updateMessageSendStatus(
            authenticationContext = authenticationContext,
            messages = messages,
            sendStatus = newStatus
        )
    }

    private suspend fun showNotifications(messages: List<ConversationWithMessageContext>) {
        //TODO()
    }

    private fun getAuthenticationContext(): AuthenticationContext? {
        return _authenticationContextState.value.getOrNull()
    }

    private fun Response<List<RawConversation>, GetMessagesError>.toConversationWithContextResponse(
        owner: Email
    ): Response<List<ConversationWithMessageContext>, GetMessagesError> {
        return when (this) {
            is Response.Failure<List<RawConversation>, GetMessagesError> -> Response.Failure(
                this.error
            )

            is Response.Success<List<RawConversation>, GetMessagesError> -> Response.Success(
                data = this@toConversationWithContextResponse.data.map { value ->
                    value.toConversationWithContext(owner = owner)
                }
            )
        }
    }
}