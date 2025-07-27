package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.user.Email

data class MessageStatusUpdate(
    val target: Email,
    val messageId: MessageId,
    val sendStatus: SendStatus,
    val hasNotificationCounter: Boolean
)
