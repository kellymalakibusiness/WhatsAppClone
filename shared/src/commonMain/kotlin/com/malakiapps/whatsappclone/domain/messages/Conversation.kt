package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.user.Email

data class Conversation(
    val contact1: Email,
    val contact2: Email,
    val messages: List<Message>
)
