package com.malakiapps.whatsappclone.domain.screens

import com.malakiapps.whatsappclone.domain.messages.ConversationWithMessageContext
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.TimeValue
import kotlinx.datetime.*
import kotlinx.datetime.LocalDateTime
import kotlin.time.ExperimentalTime

data class ChatsScreenConversationRow(
    val email: Email,
    val image: Image?,
    val name: Name,
    val lastMessage: MessageValue,
    val newMessagesCount: Int?,
    val time: TimeValue
)

@OptIn(ExperimentalTime::class)
fun Pair<Profile?, ConversationWithMessageContext>.toConversationRowObject(): ChatsScreenConversationRow {
    return ChatsScreenConversationRow(
        email = first?.email ?: ANONYMOUS_EMAIL,
        image = first?.image,
        name = first?.name ?: Name("Invalid User"),
        lastMessage = second.messages.first().value,
        newMessagesCount = if(second.noOfUnreadMessages > 0) second.noOfUnreadMessages else null,
        time = second.time.getTimeValue(today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    )
}

fun LocalDateTime.getTimeValue(today: LocalDate): TimeValue {
    return if(isToday(todayDate = today)){
        TimeValue("${time.hour}:${time.minute}")
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