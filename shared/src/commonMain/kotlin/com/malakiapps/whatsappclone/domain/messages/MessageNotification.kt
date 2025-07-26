package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name

data class MessageNotification(
    val targetImage: Image?,
    val messageId: MessageId,
    val senderEmail: Email,
    val name: Name,
    val message: MessageValue
)
