package com.malakiapps.whatsappclone.domain.screens

import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.TimeValue

sealed interface MessageCard{
    val key: String
}

data class ConversationMessage(
    override val key: String = messageId.value,
    val messageId: MessageId,
    val message: MessageValue,
    val time: TimeValue,
    val previousMessageType: MessageType,
    val sendStatus: SendStatus,
    val messageType: MessageType,
    val isStartOfReply: Boolean,
    val isSelected: Boolean = false
): MessageCard

data class TimeCard(
    override val key: String = time.value,
    val time: TimeValue
): MessageCard


enum class MessageType {
    SENT,
    RECEIVED,
    None
}

fun Message.getMessageType(target: Email?): MessageType {
    if (sender == receiver || receiver == target){
        return MessageType.SENT
    }
    return if(target == sender){
        MessageType.RECEIVED
    } else {
        MessageType.None
    }
}