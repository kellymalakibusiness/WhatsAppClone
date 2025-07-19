package com.malakiapps.whatsappclone.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.MessageAttributes
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.datetime.LocalDateTime

@Entity
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val messageId: Int = 0,
    val sender: Email,
    val receiver: Email,
    val value: MessageValue,
    val time: LocalDateTime,
    val updated: Boolean,
    val sendStatus: String,
    val isDeleted: Boolean,
    val senderReaction: String?
)

fun MessageEntity.toMessage(): Message = Message(
    messageId = MessageId(messageId.toString()),
    sender = sender,
    receiver = receiver,
    value = value,
    time = time,
    attributes = MessageAttributes(
        updated = updated,
        sendStatus = SendStatus.valueOf(sendStatus),
        isDeleted = isDeleted,
        senderReaction = senderReaction,
        receiverReaction = null
    )
)

fun Message.toMessageEntity() = MessageEntity(
            sender = sender,
            receiver = receiver,
            value = value,
            time = time,
            updated = attributes.updated,
            sendStatus = attributes.sendStatus.name,
            isDeleted = attributes.isDeleted,
            senderReaction = attributes.senderReaction,
)

class MessageValueConverter {
    @TypeConverter
    fun from(value: MessageValue): String = value.value

    @TypeConverter
    fun to(value: String): MessageValue = MessageValue(value)
}

class LocalDateTimeConverter {
    @TypeConverter
    fun from(value: LocalDateTime): String = value.toString()

    @TypeConverter
    fun to(value: String): LocalDateTime = LocalDateTime.parse(value)
}