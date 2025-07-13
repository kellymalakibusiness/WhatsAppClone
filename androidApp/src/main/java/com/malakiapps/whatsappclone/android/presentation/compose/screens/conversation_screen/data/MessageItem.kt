package com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.data

import com.malakiapps.whatsappclone.domain.messages.SendStatus

sealed interface MessageItem{
    val lastMessageWas: LastMessageWas
}

data class ReceivedMessageItem(
    override val lastMessageWas: LastMessageWas,
    val message: String,
    val time: String,
): MessageItem

data class SentMessageItem(
    override val lastMessageWas: LastMessageWas,
    val message: String,
    val time: String,
    val sendStatus: SendStatus
): MessageItem

enum class LastMessageWas {
    SENT,
    RECEIVED,
    None
}