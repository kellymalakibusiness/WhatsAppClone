package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.user.Email

data class RawConversation(
    val contact1: Email,
    val contact2: Email,
    val messages: List<Message>,
    val hasPendingWrites: Boolean
)