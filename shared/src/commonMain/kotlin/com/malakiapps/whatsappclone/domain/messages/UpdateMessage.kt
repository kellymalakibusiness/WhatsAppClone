package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.user.Email

sealed interface UpdateMessage{
    val sender: Email
    val receiver: Email
    val messageId: MessageId
}

data class ChangeMessageBody(
    override val sender: Email,
    override val receiver: Email,
    override val messageId: MessageId,
    val updatedValue: String
): UpdateMessage

data class DeleteMessageForBoth(
    override val sender: Email,
    override val receiver: Email,
    override val messageId: MessageId,
): UpdateMessage

data class ReactToMessage(
    override val sender: Email,
    override val receiver: Email,
    override val messageId: MessageId,
    val addReaction: String,
    val isSender: Boolean = false,
): UpdateMessage