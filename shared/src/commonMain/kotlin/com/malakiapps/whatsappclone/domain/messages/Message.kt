package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

data class Message(
    val messageId: MessageId = MessageId(""),
    val sender: Email,
    val receiver: Email,
    val value: MessageValue,
    val time: LocalDateTime,//Change to date type
    val attributes: MessageAttributes
)

data class MessageAttributes(
    val updated: Boolean,
    val sendStatus: SendStatus,
    val isDeleted: Boolean,
    val senderReaction: String?,
    val receiverReaction: String?
){
    companion object {
        fun generateDefaultMessageAttributes() = MessageAttributes(
            updated = false,
            sendStatus = SendStatus.ONE_TICK,
            isDeleted = false,
            senderReaction = null,
            receiverReaction = null
        )
    }
}

@JvmInline
@Serializable
value class MessageId(
    val value: String
)

@JvmInline
@Serializable
value class MessageValue(
    val value: String
)

enum class SendStatus {
    LOADING,
    ONE_TICK,
    TWO_TICKS
}

enum class MessageUpdateType {
    NEW_MESSAGE,
    UPDATED_MESSAGE,
    DELETED_MESSAGE
}