package com.malakiapps.whatsappclone.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.malakiapps.whatsappclone.data.common.getConversationReference
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
import com.malakiapps.whatsappclone.domain.messages.ChangeMessageBody
import com.malakiapps.whatsappclone.domain.messages.MessagesRepository
import com.malakiapps.whatsappclone.domain.messages.Conversation
import com.malakiapps.whatsappclone.domain.messages.DeleteMessage
import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.MessageAttributes
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageUpdateType
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.ReactToMessage
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.messages.UpdateMessage
import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val MESSAGE_LIMIT = 20L

class FirebaseFirestoreMessagesRepository(

) : MessagesRepository {
    private val firestore = Firebase.firestore

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getConversation(
        owner: Email,
        target: Email,
        paginate: Paginate?
    ): Response<Conversation, GetMessagesError> {
        return suspendCancellableCoroutine { continuation ->
            val query = firestore
                .getConversationReference(owner = owner, target = target)
                .orderBy(MessageAttributeKeys.TIME, Query.Direction.DESCENDING)
                .limit(MESSAGE_LIMIT)

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
                    val conversation = Conversation(contact1 = owner, contact2 = target, messages = messages)
                    continuation.resume(Response.Success(conversation), null)
                }
                .addOnFailureListener {
                    continuation.resume(Response.Failure(UnknownError(it)), null)
                }
        }
    }

    override suspend fun listenForMessagesChanges(
        owner: Email,
        target: Email
    ): Flow<Response<Conversation, GetMessagesError>> {
        return callbackFlow {
            firestore
                .getConversationReference(owner = owner, target = target)
                .orderBy(MessageAttributeKeys.TIME, Query.Direction.DESCENDING)
                .limit(MESSAGE_LIMIT)
                .addSnapshotListener { snaphotResponse, error ->
                    if(error != null){
                        trySend(Response.Failure(UnknownError(error)))
                    }

                    if(snaphotResponse != null){
                        val messages = snaphotResponse.documents.mapNotNull { it.toMessage().getOrNull() }
                        val conversation = Conversation(contact1 = owner, contact2 = target, messages = messages)
                        trySend(Response.Success(conversation))
                    }
                }
        }
    }

    override suspend fun listenForNewUserMessages(owner: Email): Flow<Response<List<Pair<MessageUpdateType, Message>>, GetMessagesError>> {
        return callbackFlow {
            firestore
                .collectionGroup(MESSAGES_COLLECTION_NAME)
                .orderBy(MessageAttributeKeys.TIME, Query.Direction.DESCENDING)
                .startAfter(MessageAttributeKeys.TIME, Timestamp.now())
                .addSnapshotListener { snapShotResponse, error ->
                    if(error != null){
                        trySend(Response.Failure(UnknownError(error)))
                    }

                    if(snapShotResponse != null){
                        val newChanges = snapShotResponse.documentChanges.mapNotNull { changedDocument ->
                            val changeType = when(changedDocument.type){
                                DocumentChange.Type.ADDED -> MessageUpdateType.NEW_MESSAGE
                                DocumentChange.Type.MODIFIED -> MessageUpdateType.UPDATED_MESSAGE
                                DocumentChange.Type.REMOVED -> MessageUpdateType.DELETED_MESSAGE
                            }
                            changedDocument.document.toMessage().getOrNull()?.let { document ->
                                Pair(changeType, document)
                            }
                        }
                        trySend(Response.Success(newChanges))
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun sendMessage(message: Message): Response<Message, SendMessagesError> {
        val senderReference =
            firestore.getConversationReference(owner = message.sender, target = message.receiver)
                .document()
        val receiverReference =
            firestore.getConversationReference(owner = message.receiver, target = message.sender)
                .document(senderReference.id)

        return suspendCancellableCoroutine { continuation ->
            val messageMap = message.toMessageHashMap(id = MessageId(senderReference.id))
            firestore.runBatch { batch ->
                batch.set(senderReference, messageMap)
                batch.set(receiverReference, messageMap)
            }
                .addOnCompleteListener {
                    continuation.resume(Response.Success(message), null)
                }
                .addOnFailureListener {
                    continuation.resume(Response.Failure(UnknownError(it)), null)
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun updateMessage(updateMessage: UpdateMessage): Response<Unit, UpdateMessageError> {
        val updatePayload: HashMap<String, Any> = when(updateMessage){
            is ChangeMessageBody -> {
                hashMapOf(
                    MessageAttributeKeys.IS_UPDATED to true,
                    MessageAttributeKeys.VALUE to updateMessage.updatedValue
                )
            }
            is DeleteMessage -> {
                hashMapOf(
                    MessageAttributeKeys.IS_DELETED to true,
                    MessageAttributeKeys.VALUE to ""
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
                    continuation.resume(Response.Failure(UnknownError(it)), null)
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun deleteMessage(
        owner: Email,
        target: Email,
        messageId: MessageId
    ): Response<Unit, DeleteMessageError> {
        return suspendCancellableCoroutine { continuation ->
            firestore
                .getConversationReference(owner = owner, target = target)
                .document(messageId.value)
                .delete()
                .addOnCompleteListener {
                    continuation.resume(Response.Success(Unit), null)
                }
                .addOnFailureListener {
                    continuation.resume(Response.Failure(UnknownError(it)), null)
                }
        }
    }

    private fun Message.toMessageHashMap(id: MessageId): HashMap<String, Any> {
        return hashMapOf(
            MessageAttributeKeys.MESSAGE_ID to id.value,
            MessageAttributeKeys.SENDER to sender.value,
            MessageAttributeKeys.RECEIVER to receiver.value,
            MessageAttributeKeys.VALUE to value,
            MessageAttributeKeys.TIME to FieldValue.serverTimestamp(),
            MessageAttributeKeys.MESSAGE_ATTRIBUTES to hashMapOf(
                MessageAttributeKeys.IS_UPDATED to attributes.updated,
                MessageAttributeKeys.VIEW_STATUS to attributes.sendStatus.name,
                MessageAttributeKeys.IS_DELETED to attributes.isDeleted
            )
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun DocumentSnapshot.toMessage(): Response<Message, GetMessagesError> {
        return data.let { document ->
            val message = Message(
                messageId = (document?.get(MessageAttributeKeys.MESSAGE_ID) as? String)?.let { MessageId(it) } ?: return Response.Failure(
                    MessageParsingError(MessageAttributeKeys.MESSAGE_ID)),
                sender = (document[MessageAttributeKeys.SENDER] as? String)?.let { Email(it) } ?: return Response.Failure(
                    MessageParsingError(MessageAttributeKeys.SENDER)),
                receiver = (document[MessageAttributeKeys.RECEIVER] as? String)?.let { Email(it) } ?: return Response.Failure(
                    MessageParsingError(MessageAttributeKeys.RECEIVER)),
                value = (document[MessageAttributeKeys.VALUE] as? String)?.let { MessageValue(it) } ?: return Response.Failure(
                    MessageParsingError(MessageAttributeKeys.VALUE)),
                time = (document[MessageAttributeKeys.TIME] as? Timestamp)?.let {
                    Instant.fromEpochSeconds(epochSeconds = it.seconds).toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
                } ?: return Response.Failure(
                    MessageParsingError(MessageAttributeKeys.TIME)),
                attributes = (document[MessageAttributeKeys.MESSAGE_ATTRIBUTES] as? HashMap<String, Any>)?.toMessageAttributes() ?: MessageAttributes.generateDefaultMessageAttributes(),
            )

            Response.Success(message)
        }
    }

    private fun HashMap<String, Any>.toMessageAttributes(): MessageAttributes {
        val message = MessageAttributes(
            updated = (this.get(MessageAttributeKeys.IS_UPDATED) as? Boolean) ?: false,
            sendStatus = (this.get(MessageAttributeKeys.IS_UPDATED) as? String)?.let { SendStatus.valueOf(it) } ?: SendStatus.ONE_TICK,
            isDeleted = (this.get(MessageAttributeKeys.IS_DELETED) as? Boolean) ?: false,
            senderReaction = (this.get(MessageAttributeKeys.SENDER_REACTION) as? String),
            receiverReaction = (this.get(MessageAttributeKeys.RECEIVER_REACTION) as? String)
        )

        return message
    }
}