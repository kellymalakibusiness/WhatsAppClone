package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.datetime.LocalDateTime

data class ConversationCover(
    val user: Email,
    val noOfUnreadMessages: Int,
    val messageId: MessageId,
    val value: MessageValue,
    val sendStatus: SendStatus,
    val time: LocalDateTime
)
