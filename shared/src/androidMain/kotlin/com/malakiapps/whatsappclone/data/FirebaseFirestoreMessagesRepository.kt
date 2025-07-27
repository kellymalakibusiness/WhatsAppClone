package com.malakiapps.whatsappclone.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.malakiapps.whatsappclone.data.common.getContactReference
import com.malakiapps.whatsappclone.data.common.getConversationBriefReference
import com.malakiapps.whatsappclone.data.common.getConversationReference
import com.malakiapps.whatsappclone.data.common.toEmail
import com.malakiapps.whatsappclone.data.common.toLocalDateTime
import com.malakiapps.whatsappclone.data.common.toMessageId
import com.malakiapps.whatsappclone.data.common.toMessageValue
import com.malakiapps.whatsappclone.data.common.toSendStatus
import com.malakiapps.whatsappclone.domain.common.ConversationBriefAttributeKeys
import com.malakiapps.whatsappclone.domain.common.DeleteMessageError
import com.malakiapps.whatsappclone.domain.common.GetMessagesError
import com.malakiapps.whatsappclone.domain.common.MESSAGES_COLLECTION_NAME
import com.malakiapps.whatsappclone.domain.common.MessageAttributeKeys
import com.malakiapps.whatsappclone.domain.common.MessageParsingError
import com.malakiapps.whatsappclone.domain.common.Paginate
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.SendMessagesError
import com.malakiapps.whatsappclone.domain.common.UnknownError
import com.malakiapps.whatsappclone.domain.common.UpdateMessageError
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.common.loggerTag1
import com.malakiapps.whatsappclone.domain.messages.ChangeMessageBody
import com.malakiapps.whatsappclone.domain.messages.ConversationBrief
import com.malakiapps.whatsappclone.domain.messages.DeleteMessageForBoth
import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.MessageAttributes
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageStatusUpdate
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.MessagesRepository
import com.malakiapps.whatsappclone.domain.messages.RawConversation
import com.malakiapps.whatsappclone.domain.messages.ReactToMessage
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.messages.UpdateMessage
import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreMessagesRepository : MessagesRepository {
    private val firestore = Firebase.firestore


    override suspend fun getAllActiveConversations(owner: Email): Response<List<ConversationBrief>, GetMessagesError> {
        val activeConversationBriefsQueryResponse = firestore
            .getContactReference(email = owner)
            .collection(MESSAGES_COLLECTION_NAME)
            .get()
            .await()


        val generatedBriefs = activeConversationBriefsQueryResponse.documents.mapNotNull { eachDocument ->
            val brief = eachDocument.toConversationBrief(owner = owner)
            when(brief){
                is Response.Failure<ConversationBrief, GetMessagesError> -> return Response.Failure(brief.error) //Fail on any conversation error. We do not expect this
                is Response.Success<ConversationBrief, GetMessagesError> -> brief.data
            }
        }

        return Response.Success(generatedBriefs)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getConversation(
        owner: Email,
        target: Email,
        limit: Int,
        paginate: Paginate?
    ): Response<RawConversation, GetMessagesError> {
        return suspendCancellableCoroutine { continuation ->
            val query = firestore
                .getConversationReference(owner = owner, target = target)
                .orderBy(MessageAttributeKeys.TIME.value, Query.Direction.DESCENDING)
                .limit(limit.toLong())

            val response = paginate?.let {
                query
                    .startAfter(it.fromFieldName, it.fromFieldValue)
                    .get()
            } ?: run {
                query.get()
            }

            response
                .addOnSuccessListener { response ->
                    val messages = response.documents.mapNotNull { it.toMessage().getOrNull() }
                    val conversation = RawConversation(contact1 = owner, contact2 = target, messages = messages, hasPendingWrites = false)
                    continuation.resume(Response.Success(conversation), null)
                }
                .addOnFailureListener {
                    continuation.resume(Response.Failure(UnknownError(it)), null)
                }
        }
    }

    override fun listenForMessagesChanges(
        owner: Email,
        target: Email,
        limit: Int
    ): Flow<Response<RawConversation, GetMessagesError>> {
        return callbackFlow {
            val listener = firestore
                .getConversationReference(owner = owner, target = target)
                .orderBy(MessageAttributeKeys.TIME.value, Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .addSnapshotListener { snapshotResponse, error ->
                    if(error != null){
                        trySend(Response.Failure(UnknownError(error)))
                    }

                    if(snapshotResponse != null){
                        val hasPendingWrites = snapshotResponse.metadata.hasPendingWrites()
                        val messages = snapshotResponse.documents.mapNotNull { it.toMessage().getOrNull() }

                        val conversation = RawConversation(contact1 = owner, contact2 = target, messages = messages, hasPendingWrites = hasPendingWrites)
                        trySend(Response.Success(conversation))
                    }
                }

            awaitClose { listener.remove() }
        }
    }

    override fun listenForNewUserMessages(owner: Email): Flow<Response<List<ConversationBrief>, GetMessagesError>> {
        return callbackFlow {
            val listener = firestore
                .getContactReference(email = owner)
                .collection(MESSAGES_COLLECTION_NAME)
                .addSnapshotListener { snapShotResponse, error ->
                    if(error != null){
                        trySend(Response.Failure(UnknownError(error)))
                    }

                    if(snapShotResponse != null){
                        val briefs = snapShotResponse.documents.mapNotNull { briefDocuments ->
                            when(val brief = briefDocuments.toConversationBrief(owner = owner)){
                                is Response.Failure<ConversationBrief, GetMessagesError> -> {
                                    trySend(Response.Failure(brief.error))
                                    loggerTag1.i { "We got an error of ${brief.error}" }
                                    null
                                }
                                is Response.Success<ConversationBrief, GetMessagesError> -> {
                                    brief.data
                                }
                            }
                        }
                        /*val newChanges = snapShotResponse.documentChanges.mapNotNull { briefDocuments ->
                            val changeType = when(briefDocuments.type){
                                DocumentChange.Type.ADDED -> MessageUpdateType.NEW_MESSAGE
                                DocumentChange.Type.MODIFIED -> MessageUpdateType.UPDATED_MESSAGE
                                DocumentChange.Type.REMOVED -> MessageUpdateType.DELETED_MESSAGE
                            }
                            changedDocument.document.toMessage().getOrNull()?.let { document ->
                                Pair(changeType, document)
                            }
                        }*/
                        loggerTag1.i { "Got briefs from firestore $briefs" }
                        trySend(Response.Success(briefs))
                    }
                }

            awaitClose { listener.remove() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun sendMessage(message: Message): Response<Message, SendMessagesError> {
        return if(message.sender != message.receiver){
            //Message to another user
            val senderReference =
                firestore.getConversationReference(owner = message.sender, target = message.receiver)
                    .document()
            val receiverReference =
                firestore.getConversationReference(owner = message.receiver, target = message.sender)
                    .document(senderReference.id)

            val senderBriefReference = firestore.getConversationBriefReference(owner = message.sender, target = message.receiver)
            val receiverBriefReference = firestore.getConversationBriefReference(owner = message.receiver, target = message.sender)

            val messageMap = message.toMessageHashMap(id = MessageId(senderReference.id))
            val senderNewMessageCount = 0L
            val receiverNewMessageCount = FieldValue.increment(1L)
            val senderBriefUpdate = generateBriefUpdateForSendMessage(targetEmail = message.receiver, senderEmail = message.sender, messageId = MessageId(senderReference.id), messageValue = message.value, status = SendStatus.ONE_TICK, newMessageCount = senderNewMessageCount)
            val receiverBriefUpdate = generateBriefUpdateForSendMessage(targetEmail = message.sender, senderEmail = message.sender, messageId = MessageId(senderReference.id), messageValue = message.value, status = SendStatus.ONE_TICK, newMessageCount = receiverNewMessageCount)
            try {
                firestore.runBatch { batch ->
                    batch.set(senderReference, messageMap)
                    batch.set(receiverReference, messageMap)
                    batch.set(senderBriefReference, senderBriefUpdate, SetOptions.merge())
                    batch.set(receiverBriefReference, receiverBriefUpdate, SetOptions.merge())
                }.await()

                Response.Success(message)
            } catch (e: FirebaseFirestoreException){
                Response.Failure(UnknownError(e))
            }
        } else {
            //Message to self
            val selfReference = firestore.getConversationReference(owner = message.sender, target = message.receiver).document()
            val messageMap = message.toMessageHashMap(id = MessageId(selfReference.id))

            val senderBriefReference = firestore.getConversationBriefReference(owner = message.sender, target = message.receiver)
            val senderBriefUpdate = generateBriefUpdateForSendMessage(targetEmail = message.receiver, senderEmail = message.sender, messageValue = message.value, status = SendStatus.TWO_TICKS_READ, messageId = MessageId(selfReference.id), newMessageCount = 0L)

            try {
                firestore.runBatch { batch ->
                    batch.set(selfReference, messageMap)
                    batch.set(senderBriefReference, senderBriefUpdate, SetOptions.merge())
                }.await()

                Response.Success(message)
            } catch (e: FirebaseFirestoreException){
                Response.Failure(UnknownError(e))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun updateMessage(updateMessage: UpdateMessage): Response<Unit, UpdateMessageError> {
        val updatePayload: HashMap<String, Any> = when(updateMessage){
            is ChangeMessageBody -> {
                hashMapOf(
                    MessageAttributeKeys.IS_UPDATED.value to true,
                    MessageAttributeKeys.VALUE.value to updateMessage.updatedValue
                )
            }
            is DeleteMessageForBoth -> {
                hashMapOf(
                    MessageAttributeKeys.IS_DELETED.value to true,
                    MessageAttributeKeys.VALUE.value to ""
                )
            }
            is ReactToMessage -> {
                val reactorKey = if(updateMessage.isSender) MessageAttributeKeys.SENDER_REACTION else MessageAttributeKeys.RECEIVER_REACTION
                hashMapOf("${MessageAttributeKeys.MESSAGE_ATTRIBUTES}.${reactorKey}" to updateMessage.addReaction)
            }
        }

        val messageReference1 = firestore.getConversationReference(owner = updateMessage.sender, target = updateMessage.receiver).document(updateMessage.messageId.value)
        val messageReference2 = firestore.getConversationReference(owner = updateMessage.receiver, target = updateMessage.sender).document(updateMessage.messageId.value)
        return suspendCancellableCoroutine { continuation ->
            firestore.runBatch { batch ->
                batch.update(messageReference1, updatePayload)
                batch.update(messageReference2, updatePayload)
            }
                .addOnCompleteListener {
                    continuation.resume(Response.Success(Unit), null)
                }
                .addOnFailureListener {
                    //User might have deleted their own message, ignore the error
                    continuation.resume(Response.Success(Unit), null)
                }
        }
    }

    override suspend fun updateMessagesReadStatus(receiver: Email, messageStatusUpdate: List<MessageStatusUpdate>): Response<Unit, UpdateMessageError> {

        firestore.runTransaction { transaction ->
            val foundBriefs = mutableMapOf<Email, ConversationBrief>()

            val conversationBriefs = mutableMapOf<Email, ConversationBrief>()

            messageStatusUpdate.forEach { messageStatusUpdate ->
                if(foundBriefs[messageStatusUpdate.target] == null){
                    val briefReference = firestore
                        .getContactReference(email = messageStatusUpdate.target)
                        .collection(MESSAGES_COLLECTION_NAME)
                        .document(receiver.value)
                    transaction.get(briefReference).toConversationBrief(receiver).getOrNull()?.let { foundBrief ->
                        conversationBriefs.put(messageStatusUpdate.target, foundBrief)
                    }
                }
            }


            //Now we make the calls with all the info we have
            messageStatusUpdate.forEach { messageStatusUpdate ->
                val update = hashMapOf<String, Any>(
                    "${MessageAttributeKeys.MESSAGE_ATTRIBUTES}.${MessageAttributeKeys.VIEW_STATUS}" to messageStatusUpdate.sendStatus.name
                )

                val messageReference1 = firestore.getConversationReference(owner = messageStatusUpdate.target, target = receiver).document(messageStatusUpdate.messageId.value)
                val messageReference2 = firestore.getConversationReference(owner = receiver, target = messageStatusUpdate.target).document(messageStatusUpdate.messageId.value)

                transaction.update(messageReference1, update)
                transaction.update(messageReference2, update)

                conversationBriefs[messageStatusUpdate.target]?.let { availableBrief ->
                    val briefReference = firestore
                        .getContactReference(email = messageStatusUpdate.target)
                        .collection(MESSAGES_COLLECTION_NAME)
                        .document(receiver.value)
                        if(availableBrief.messageId == messageStatusUpdate.messageId){
                            //We need to update the briefs too.
                            transaction.update(briefReference, ConversationBriefAttributeKeys.VIEW_STATUS.value, messageStatusUpdate.sendStatus.name,
                                ConversationBriefAttributeKeys.NEW_MESSAGE_COUNT.value, (if (messageStatusUpdate.hasNotificationCounter) FieldValue.increment(1L) else 0L))

                            //For receiver brief add the counter
                        }
                    }
                }
        }.await()

        return Response.Success(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun deleteMessages(
        owner: Email,
        target: Email,
        messageIds: List<MessageId>
    ): Response<Unit, DeleteMessageError> {
        return suspendCancellableCoroutine { continuation ->
            if (messageIds.size == 1) {
                firestore
                    .getConversationReference(owner = owner, target = target)
                    .document(messageIds.first().value)
                    .delete()
                    .addOnCompleteListener {
                        continuation.resume(Response.Success(Unit), null)
                    }
                    .addOnFailureListener {
                        continuation.resume(Response.Failure(UnknownError(it)), null)
                    }
            } else {
                firestore.runBatch { batch ->
                    messageIds.forEach { messageId ->
                        batch.delete(
                            firestore
                                .getConversationReference(owner = owner, target = target)
                                .document(messageId.value)
                        )
                    }
                }
            }
        }
    }

    override suspend fun importAllUserMessages(owner: Email, rawConversation: RawConversation, conversationBrief: ConversationBrief): Response<Unit, SendMessagesError> {
        //Add a limit to prevent users from going too crazy
        val filteredMessages = rawConversation.messages.take(25)
        val selfCollectionReference = firestore
            .getConversationReference(owner = owner, target = owner)
        val briefReference = firestore.getConversationBriefReference(owner = owner, target = owner)
        val messageBrief = generateBriefUpdateForSendMessage(targetEmail = owner, senderEmail = conversationBrief.sender, messageId = conversationBrief.messageId, messageValue = conversationBrief.value, status = conversationBrief.sendStatus, newMessageCount = 0L)

        try {
            firestore.runBatch { batch ->
                batch.set(briefReference, messageBrief, SetOptions.merge())
                filteredMessages.forEach {
                    val documentRef = selfCollectionReference.document()
                    batch.set(documentRef, it.toMessageHashMap(id = MessageId(documentRef.id)))
                }
            }.await()
        } catch (e: FirebaseFirestoreException){
            return Response.Failure(UnknownError(e))
        }

        return Response.Success(Unit)
    }

    private fun Message.toMessageHashMap(id: MessageId): HashMap<String, Any> {
        return hashMapOf(
            MessageAttributeKeys.MESSAGE_ID.value to id.value,
            MessageAttributeKeys.SENDER.value to sender.value,
            MessageAttributeKeys.RECEIVER.value to receiver.value,
            MessageAttributeKeys.VALUE.value to value.value,
            MessageAttributeKeys.TIME.value to FieldValue.serverTimestamp(),
            MessageAttributeKeys.MESSAGE_ATTRIBUTES.value to hashMapOf(
                MessageAttributeKeys.IS_UPDATED.value to attributes.updated,
                MessageAttributeKeys.VIEW_STATUS.value to attributes.sendStatus.name,
                MessageAttributeKeys.IS_DELETED.value to attributes.isDeleted
            )
        )
    }

    private fun DocumentSnapshot.toMessage(): Response<Message, GetMessagesError> {
        return data.let { document ->

            val message = Message(
                messageId = document?.get(MessageAttributeKeys.MESSAGE_ID.value).toMessageId() ?: return MessageAttributeKeys.MESSAGE_ID.toParsingError(),
                sender = document?.get(MessageAttributeKeys.SENDER.value).toEmail() ?: return MessageAttributeKeys.SENDER.toParsingError(),
                receiver = document?.get(MessageAttributeKeys.RECEIVER.value).toEmail() ?: return MessageAttributeKeys.RECEIVER.toParsingError(),
                value = document?.get(MessageAttributeKeys.VALUE.value).toMessageValue() ?: return MessageAttributeKeys.VALUE.toParsingError(),
                time = document?.get(MessageAttributeKeys.TIME.value).toLocalDateTime() ?: return MessageAttributeKeys.TIME.toParsingError(),
                attributes = (document?.get(MessageAttributeKeys.MESSAGE_ATTRIBUTES.value) as? Map<String, Any>)?.toMessageAttributes() ?: MessageAttributes.generateDefaultMessageAttributes(),
            )

            Response.Success(message)
        }
    }

    private fun Map<String, Any>.toMessageAttributes(): MessageAttributes {
        val message = MessageAttributes(
            updated = (this[MessageAttributeKeys.IS_UPDATED.value] as? Boolean) ?: false,
            sendStatus = this[MessageAttributeKeys.VIEW_STATUS.value].toSendStatus() ?: SendStatus.ONE_TICK,
            isDeleted = (this[MessageAttributeKeys.IS_DELETED.value] as? Boolean) ?: false,
            senderReaction = (this[MessageAttributeKeys.SENDER_REACTION.value] as? String),
            receiverReaction = (this[MessageAttributeKeys.RECEIVER_REACTION.value] as? String)
        )

        return message
    }

    private fun generateBriefUpdateForSendMessage(targetEmail: Email, senderEmail: Email, messageId: MessageId, messageValue: MessageValue, status: SendStatus, newMessageCount: Any): Map<String, Any> {
        return hashMapOf(
            ConversationBriefAttributeKeys.NEW_MESSAGE_COUNT.value to newMessageCount,
            ConversationBriefAttributeKeys.MESSAGE_ID.value to messageId.value,
            ConversationBriefAttributeKeys.TARGET_EMAIL.value to targetEmail.value,
            ConversationBriefAttributeKeys.SENDER_EMAIL.value to senderEmail.value,
            ConversationBriefAttributeKeys.MESSAGE_VALUE.value to messageValue.value,
            ConversationBriefAttributeKeys.VIEW_STATUS.value to status.name,
            ConversationBriefAttributeKeys.TIME.value to FieldValue.serverTimestamp()
        )
    }

    private fun DocumentSnapshot.toConversationBrief(owner: Email): Response<ConversationBrief, GetMessagesError> {
        return data.let { document ->
            val target = document?.get(ConversationBriefAttributeKeys.TARGET_EMAIL.value)?.toEmail() ?: return ConversationBriefAttributeKeys.TARGET_EMAIL.toParsingError()
            val conversationBrief = ConversationBrief(
                target = target,
                newMessageCount = (document[ConversationBriefAttributeKeys.NEW_MESSAGE_COUNT.value] as? Long)?.toInt() ?: return ConversationBriefAttributeKeys.NEW_MESSAGE_COUNT.toParsingError(),
                messageId = document[ConversationBriefAttributeKeys.MESSAGE_ID.value].toMessageId() ?: return ConversationBriefAttributeKeys.MESSAGE_ID.toParsingError(),
                sender = document[ConversationBriefAttributeKeys.SENDER_EMAIL.value].toEmail() ?: return ConversationBriefAttributeKeys.SENDER_EMAIL.toParsingError(),
                value = document[ConversationBriefAttributeKeys.MESSAGE_VALUE.value].toMessageValue() ?: return ConversationBriefAttributeKeys.MESSAGE_VALUE.toParsingError(),
                sendStatus = document[ConversationBriefAttributeKeys.VIEW_STATUS.value].toSendStatus() ?: return ConversationBriefAttributeKeys.VIEW_STATUS.toParsingError(),
                time =document[ConversationBriefAttributeKeys.TIME.value].toLocalDateTime() ?: return ConversationBriefAttributeKeys.TIME.toParsingError(),
                isSelfMessage = target == owner
            )

            Response.Success(conversationBrief)
        }
    }

    private fun MessageAttributeKeys.toParsingError(): Response<Message, GetMessagesError> = Response.Failure(MessageParsingError(this.value))
    private fun ConversationBriefAttributeKeys.toParsingError(): Response<ConversationBrief, GetMessagesError> = Response.Failure(MessageParsingError(this.value))
}