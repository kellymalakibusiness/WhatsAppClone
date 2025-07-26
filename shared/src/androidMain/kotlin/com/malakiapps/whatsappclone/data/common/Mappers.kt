package com.malakiapps.whatsappclone.data.common

import com.google.firebase.Timestamp
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun Any?.toEmail(): Email? = (this as? String)?.let { Email(it) }

fun Any?.toMessageId(): MessageId? = (this as? String)?.let { MessageId(it) }

fun Any?.toMessageValue(): MessageValue? = (this as? String)?.let { MessageValue(it) }

fun Any?.toSendStatus(): SendStatus? = (this as? String)?.let { SendStatus.valueOf(it) }

@OptIn(ExperimentalTime::class)
fun Any?.toLocalDateTime(): LocalDateTime? = (this as? Timestamp)?.let {
    Instant.fromEpochSeconds(epochSeconds = it.seconds).toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
}