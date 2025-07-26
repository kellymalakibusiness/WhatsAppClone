package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.datetime.LocalDateTime

data class ConversationBrief(
    val newMessageCount: Int,
    val messageId: MessageId,
    val sender: Email,
    val value: MessageValue,
    val sendStatus: SendStatus,
    val time: LocalDateTime
)