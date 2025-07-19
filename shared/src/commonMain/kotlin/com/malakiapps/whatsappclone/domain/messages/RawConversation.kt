package com.malakiapps.whatsappclone.domain.messages

import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.TimeValue
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class ConversationWithMessageContext(
    val contact1: Email,
    val contact2: Email,
    val messages: List<Message>,
    val noOfUnreadMessages: Int,
    val time: LocalDateTime
)

data class RawConversation(
    val contact1: Email,
    val contact2: Email,
    val messages: List<Message>
)

fun ConversationWithMessageContext.toRawConversation() = RawConversation(
    contact1 = contact1,
    contact2 = contact2,
    messages = messages
)

@OptIn(ExperimentalTime::class)
fun RawConversation.toConversationWithContext(owner: Email): ConversationWithMessageContext {
    return ConversationWithMessageContext(
        contact1 = contact1,
        contact2 = contact2,
        messages = messages,
        noOfUnreadMessages = messages.count { !it.isMessageRead(owner = owner) },
        time = messages.firstOrNull()?.time ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    )
}