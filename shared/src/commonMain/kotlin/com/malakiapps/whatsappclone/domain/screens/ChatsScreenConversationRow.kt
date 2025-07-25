package com.malakiapps.whatsappclone.domain.screens

import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.TimeValue
import com.malakiapps.whatsappclone.domain.user.TimeValue.Companion.toParsedTimeValue
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

data class ChatsScreenConversationRow(
    val email: Email,
    val image: Image?,
    val name: Name,
    val lastMessage: MessageValue?,
    val newMessagesCount: Int,
    val isMyMessage: Boolean,
    val sendStatus: SendStatus,
    val time: TimeValue
)

fun LocalDateTime.getTimeValue(today: LocalDate): TimeValue {
    return if(isToday(todayDate = today)){
        time.toParsedTimeValue()
    } else if(isYesterday(todayDate = today)){
        TimeValue("Yesterday")
    } else if(isThisWeek(todayDate = today)){
        TimeValue(date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() })
    } else {
        TimeValue("${date.day}/${date.month.number}/${date.year}")
    }
}

fun LocalDateTime.getDayValue(today: LocalDate): TimeValue {
    return if(isToday(todayDate = today)){
        TimeValue("Today")
    } else if(isYesterday(todayDate = today)){
        TimeValue("Yesterday")
    } else if(isThisWeek(todayDate = today)){
        TimeValue(date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() })
    } else {
        TimeValue("${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.day}, ${date.year}")
    }
}

private fun LocalDateTime.isToday(todayDate: LocalDate): Boolean {
    return this.date == todayDate
}

private fun LocalDateTime.isYesterday(todayDate: LocalDate): Boolean {
    return this.date.toEpochDays()-1 == todayDate.toEpochDays()
}

private fun LocalDateTime.isThisWeek(todayDate: LocalDate): Boolean {
    return (todayDate.toEpochDays() - date.toEpochDays()) < 7
}