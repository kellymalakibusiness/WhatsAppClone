package common

import com.malakiapps.whatsappclone.domain.messages.Message
import com.malakiapps.whatsappclone.domain.messages.MessageAttributes
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.user.Email

private val messageAttributes = MessageAttributes(
    updated = false,
    sendStatus = SendStatus.TWO_TICKS_READ,
    isDeleted = false,
    senderReaction = null,
    receiverReaction = null
)

val ownerUser = Email("ownerUser")
val targetUser = Email("targetUser")

val day1Message1 = Message(
    messageId = MessageId("15"),
    sender = ownerUser,
    receiver = targetUser,
    value = MessageValue("16"),
    time = day1Time1,
    attributes = messageAttributes
)
val day1Message2 = Message(
    messageId = MessageId("14"),
    sender = ownerUser,
    receiver = targetUser,
    value = MessageValue("15"),
    time = day1Time2,
    attributes = messageAttributes
)
val day1Message3 = Message(
    messageId = MessageId("13"),
    sender = ownerUser,
    receiver = targetUser,
    value = MessageValue("14"),
    time = day1Time3,
    attributes = messageAttributes
)
val day1Message4 = Message(
    messageId = MessageId("12"),
    sender = ownerUser,
    receiver = targetUser,
    value = MessageValue("13"),
    time = day1Time4,
    attributes = messageAttributes
)


val day2Message1 = Message(
    messageId = MessageId("11"),
    sender = targetUser,
    receiver = ownerUser,
    value = MessageValue("12"),
    time = day2Time1,
    attributes = messageAttributes
)
val day2Message2 = Message(
    messageId = MessageId("10"),
    sender = targetUser,
    receiver = ownerUser,
    value = MessageValue("11"),
    time = day2Time2,
    attributes = messageAttributes
)
val day2Message3 = Message(
    messageId = MessageId("9"),
    sender = targetUser,
    receiver = ownerUser,
    value = MessageValue("10"),
    time = day2Time3,
    attributes = messageAttributes
)
val day2Message4 = Message(
    messageId = MessageId("8"),
    sender = targetUser,
    receiver = ownerUser,
    value = MessageValue("9"),
    time = day2Time4,
    attributes = messageAttributes
)


val day3Message1 = Message(
    messageId = MessageId("7"),
    sender = targetUser,
    receiver = ownerUser,
    value = MessageValue("8"),
    time = day3Time1,
    attributes = messageAttributes
)
val day3Message2 = Message(
    messageId = MessageId("6"),
    sender = targetUser,
    receiver = ownerUser,
    value = MessageValue("7"),
    time = day3Time2,
    attributes = messageAttributes
)
val day3Message3 = Message(
    messageId = MessageId("5"),
    sender = ownerUser,
    receiver = targetUser,
    value = MessageValue("6"),
    time = day3Time3,
    attributes = messageAttributes
)
val day3Message4 = Message(
    messageId = MessageId("4"),
    sender = ownerUser,
    receiver = targetUser,
    value = MessageValue("5"),
    time = day3Time4,
    attributes = messageAttributes
)



val day4Message1 = Message(
    messageId = MessageId("3"),
    sender = targetUser,
    receiver = ownerUser,
    value = MessageValue("4"),
    time = day4Time1,
    attributes = messageAttributes
)
val day4Message2 = Message(
    messageId = MessageId("2"),
    sender = ownerUser,
    receiver = targetUser,
    value = MessageValue("3"),
    time = day4Time2,
    attributes = messageAttributes
)
val day4Message3 = Message(
    messageId = MessageId("1"),
    sender = targetUser,
    receiver = ownerUser,
    value = MessageValue("2"),
    time = day4Time3,
    attributes = messageAttributes
)
val day4Message4 = Message(
    messageId = MessageId("0"),
    sender = ownerUser,
    receiver = targetUser,
    value = MessageValue("1"),
    time = day4Time4,
    attributes = messageAttributes
)