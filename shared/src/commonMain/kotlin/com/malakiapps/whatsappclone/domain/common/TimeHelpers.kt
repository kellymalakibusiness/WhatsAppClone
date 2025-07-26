package com.malakiapps.whatsappclone.domain.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun getTodayLocalDateTime(): LocalDateTime {
    return kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

fun getTodayLocalDate(): LocalDate {
    return getTodayLocalDateTime().date
}