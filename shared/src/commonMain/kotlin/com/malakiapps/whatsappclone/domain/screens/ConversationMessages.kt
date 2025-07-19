package com.malakiapps.whatsappclone.domain.screens

import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.TimeValue

sealed interface MessageCard

data class ConversationMessage(
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
    val time: TimeValue
): MessageCard


enum class MessageType {
    SENT,
    RECEIVED,
    None
}

fun Message.getMessageType(target: Email?): MessageType {
    return if(target == sender){
        MessageType.RECEIVED
    } else {
        MessageType.SENT
    }
}