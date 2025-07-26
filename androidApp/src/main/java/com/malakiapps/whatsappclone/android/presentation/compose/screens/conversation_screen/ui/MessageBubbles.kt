package com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.screens.ConversationMessage
import com.malakiapps.whatsappclone.domain.screens.MessageType
import com.malakiapps.whatsappclone.domain.screens.TimeCard
import com.malakiapps.whatsappclone.domain.user.TimeValue

@Composable
fun ReceivedMessageBubble(conversationMessage: ConversationMessage, modifier: Modifier = Modifier) {
    var messageBottomPadding by remember { mutableStateOf(0.dp) }
    val ninetyPercentOfScreen = (LocalConfiguration.current.screenWidthDp * 0.9f).dp
    val bubbleShape = if (conversationMessage.isStartOfReply) {
        ReceivedMessageShape()
    } else {
        RoundedCornerShape(8.dp)
    }
    val sizedModifier = modifier.requiredSizeIn(
        minWidth = 120.dp,
        maxWidth = ninetyPercentOfScreen,
        minHeight = 40.dp,
        maxHeight = Dp.Infinity
    )

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = bubbleShape,
            tonalElevation = 16.dp,
            shadowElevation = 0.8.dp,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(
                    bottom = if (conversationMessage.previousMessageType != MessageType.RECEIVED) {
                        8.dp
                    } else {
                        2.dp
                    },
                    start = if (conversationMessage.isStartOfReply) {
                        0.dp
                    } else {
                        8.dp
                    }
                )
        ) {
            Box(
                modifier = sizedModifier.padding(
                    start = if (conversationMessage.isStartOfReply) {
                        8.dp
                    } else {
                        0.dp
                    }
                )
            ) {
                val rect1 = remember { mutableStateOf<Rect?>(null) }
                val rect2 = remember { mutableStateOf<Rect?>(null) }
                val messageStartPadding = if (conversationMessage.isStartOfReply) {
                    22.dp
                } else {
                    14.dp
                }
                Text(
                    text = conversationMessage.message.value,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = messageBottomPadding)
                        .padding(start = messageStartPadding, top = 8.dp, end = 8.dp, bottom = 8.dp)
                        .onGloballyPositioned(onGloballyPositioned = {
                            rect1.value = it.boundsInWindow()
                        })
                )


                Text(
                    text = conversationMessage.time.value,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 4.dp)
                        .onGloballyPositioned(onGloballyPositioned = {
                            rect2.value = it.boundsInWindow()
                        }),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                LaunchedEffect(rect1.value, rect2.value) {
                    val r1 = rect1.value
                    val r2 = rect2.value
                    if (r1 != null && r2 != null && r1.overlaps(r2)) {
                        //Now check if we're in the second or more row
                        messageBottomPadding += 12.dp
                    }
                }
            }
        }
        if (conversationMessage.isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            )
        }
    }

}

@Composable
fun SentMessageBubble(conversationMessage: ConversationMessage, modifier: Modifier = Modifier) {
    var messageBottomPadding by remember { mutableStateOf(0.dp) }
    val ninetyPercentOfScreen = (LocalConfiguration.current.screenWidthDp * 0.9f).dp
    val bubbleShape = if (conversationMessage.isStartOfReply) {
        SentMessageShape()
    } else {
        RoundedCornerShape(8.dp)
    }
    val sizedModifier = modifier.requiredSizeIn(
        minWidth = 120.dp,
        maxWidth = ninetyPercentOfScreen,
        minHeight = 40.dp,
        maxHeight = Dp.Infinity
    )

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(
                    bottom = if (conversationMessage.previousMessageType != MessageType.SENT) {
                        8.dp
                    } else {
                        2.dp
                    }
                )
        ) {
            Spacer(Modifier.weight(1f))
            Surface(
                shape = bubbleShape,
                tonalElevation = 16.dp,
                shadowElevation = 0.8.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.padding(
                    end = if (conversationMessage.isStartOfReply) {
                        0.dp
                    } else {
                        8.dp
                    }
                )
            ) {
                Box(
                    modifier = sizedModifier
                ) {
                    val rect1 = remember { mutableStateOf<Rect?>(null) }
                    val rect2 = remember { mutableStateOf<Rect?>(null) }
                    val messageEndPadding = if (conversationMessage.isStartOfReply) {
                        14.dp
                    } else {
                        22.dp
                    }
                    Text(
                        text = conversationMessage.message.value,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(bottom = messageBottomPadding)
                            .padding(
                                end = messageEndPadding,
                                top = 8.dp,
                                start = 8.dp,
                                bottom = 8.dp
                            )
                            .onGloballyPositioned(onGloballyPositioned = {
                                rect1.value = it.boundsInWindow()
                            })
                    )


                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,

                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(
                                end = if (conversationMessage.isStartOfReply) {
                                    16.dp
                                } else {
                                    8.dp
                                }, bottom = 4.dp
                            )
                            .onGloballyPositioned(onGloballyPositioned = {
                                rect2.value = it.boundsInWindow()
                            })
                    ) {
                        Text(
                            text = conversationMessage.time.value,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Icon(
                            painter = painterResource(
                                when (conversationMessage.sendStatus) {
                                    SendStatus.LOADING -> R.drawable.loading_icon
                                    SendStatus.ONE_TICK -> R.drawable.outline_check_24
                                    SendStatus.TWO_TICKS -> R.drawable.double_tick_icon
                                    SendStatus.TWO_TICKS_READ -> R.drawable.blue_double_tick_icon
                                }
                            ),
                            modifier = Modifier.size(18.dp),
                            contentDescription = null,
                            tint = if (conversationMessage.sendStatus == SendStatus.TWO_TICKS_READ) {
                                Color(0xff4097e7)
                            } else {
                                MaterialTheme.colorScheme.secondary
                            }
                        )
                    }

                    LaunchedEffect(rect1.value, rect2.value) {
                        val r1 = rect1.value
                        val r2 = rect2.value
                        if (r1 != null && r2 != null && r1.overlaps(r2)) {
                            //Now check if we're in the second or more row
                            messageBottomPadding += 12.dp
                        }
                    }
                }
            }
        }
        if (conversationMessage.isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            )
        }
    }
}

@Composable
fun TimeBubble(timeCard: TimeCard, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 16.dp,
            shadowElevation = 0.8.dp,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = timeCard.time.value,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Preview//(uiMode = 33)
@Composable
private fun SentMessageShapePrev() {
    val messages = listOf(
        ConversationMessage(
            messageId = MessageId(""),
            message = MessageValue("Message 0 from another person"),
            previousMessageType = MessageType.None,
            messageType = MessageType.RECEIVED,
            sendStatus = SendStatus.TWO_TICKS_READ,
            time = TimeValue("12:45"),
            isStartOfReply = false,
            isSelected = false,
        ),
        TimeCard(
            key = "12345",
            TimeValue("Today")
        ),
        ConversationMessage(
            messageId = MessageId(""),
            message = MessageValue("Message 1 from another person"),
            previousMessageType = MessageType.None,
            messageType = MessageType.RECEIVED,
            sendStatus = SendStatus.TWO_TICKS_READ,
            time = TimeValue("12:45"),
            isStartOfReply = false,
            isSelected = false,
        ),
        ConversationMessage(
            messageId = MessageId(""),
            message = MessageValue("Message 2 from another person"),
            previousMessageType = MessageType.RECEIVED,
            messageType = MessageType.RECEIVED,
            sendStatus = SendStatus.TWO_TICKS_READ,
            time = TimeValue("12:45"),
            isStartOfReply = true,
            isSelected = false,
        ),
        ConversationMessage(
            messageId = MessageId(""),
            message = MessageValue("Message from me, not read"),
            messageType = MessageType.SENT,
            previousMessageType = MessageType.RECEIVED,
            time = TimeValue("12:47"),
            sendStatus = SendStatus.TWO_TICKS,
            isStartOfReply = false,
            isSelected = false,
        ),
        ConversationMessage(
            messageId = MessageId(""),
            message = MessageValue("Another message from me, read"),
            messageType = MessageType.SENT,
            previousMessageType = MessageType.SENT,
            time = TimeValue("12:47"),
            sendStatus = SendStatus.TWO_TICKS_READ,
            isStartOfReply = true,
            isSelected = true,
        ),
        ConversationMessage(
            messageId = MessageId(""),
            message = MessageValue("Message from another person😂"),
            messageType = MessageType.RECEIVED,
            previousMessageType = MessageType.SENT,
            time = TimeValue("12:48"),
            sendStatus = SendStatus.TWO_TICKS,
            isStartOfReply = true,
            isSelected = true,
        )
    )

    val list2 = listOf(
        ConversationMessage(
            messageId = MessageId(""),
            previousMessageType = MessageType.SENT,
            message = MessageValue("Fourteen"),
            messageType = MessageType.SENT,
            time = TimeValue("12:49"),
            sendStatus = SendStatus.LOADING,
            isStartOfReply = true,
            isSelected = false,
        ),
        ConversationMessage(
            messageId = MessageId(""),
            messageType = MessageType.SENT,
            message = MessageValue("Twelve😂"),
            previousMessageType = MessageType.SENT,
            time = TimeValue("12:48"),
            sendStatus = SendStatus.ONE_TICK,
            isStartOfReply = false,
            isSelected = false,
        ),
        ConversationMessage(
            messageId = MessageId(""),
            messageType = MessageType.SENT,
            previousMessageType = MessageType.RECEIVED,
            message = MessageValue("Eight nine ten eleven"),
            time = TimeValue("14:29"),
            sendStatus = SendStatus.TWO_TICKS,
            isStartOfReply = true,
            isSelected = false,
        ),
        ConversationMessage(
            messageType = MessageType.RECEIVED,
            previousMessageType = MessageType.None,
            message = MessageValue("One two three four five six seven"),//"Aah bummer😕. Sawa incase uipate just let me know, thanks. kelly",
            time = TimeValue("14:30"),
            messageId = MessageId(""),
            sendStatus = SendStatus.TWO_TICKS,
            isStartOfReply = true,
            isSelected = false,
        ),


        )
    FakeWhatsAppTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 16.dp)
            ) {

                LazyColumn(
                    reverseLayout = true
                ) {
                    items(items = messages) { card ->
                        when (card) {
                            is ConversationMessage -> {
                                if (card.messageType == MessageType.RECEIVED) {
                                    ReceivedMessageBubble(
                                        conversationMessage = card,
                                    )
                                } else {
                                    SentMessageBubble(
                                        conversationMessage = card
                                    )
                                }
                            }

                            is TimeCard -> {
                                TimeBubble(card)
                            }
                        }
                    }
                }
                MessageTextField(
                    messageValue = "",
                    onMessageChange = {},
                    {}, {}
                )
            }
        }
    }
}