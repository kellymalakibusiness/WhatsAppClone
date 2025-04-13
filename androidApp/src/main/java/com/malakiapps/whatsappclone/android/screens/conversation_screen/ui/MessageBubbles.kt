package com.malakiapps.whatsappclone.android.screens.conversation_screen.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.screens.conversation_screen.data.LastMessageWas
import com.malakiapps.whatsappclone.android.screens.conversation_screen.data.ReceivedMessageItem
import com.malakiapps.whatsappclone.android.screens.conversation_screen.data.SendStatus
import com.malakiapps.whatsappclone.android.screens.conversation_screen.data.SentMessageItem

@Composable
fun ReceivedMessageBubble(receivedMessageItem: ReceivedMessageItem, modifier: Modifier = Modifier) {
    var messageBottomPadding by remember { mutableStateOf(0.dp) }
    val ninetyPercentOfScreen = (LocalConfiguration.current.screenWidthDp * 0.9f).dp
    val isLastMessageReceivedOne = receivedMessageItem.lastMessageWas == LastMessageWas.RECEIVED
    val bubbleShape = if (isLastMessageReceivedOne) {
        RoundedCornerShape(8.dp)
    } else {
        ReceivedMessageShape()
    }
    val sizedModifier = modifier.requiredSizeIn(
            minWidth = 120.dp,
            maxWidth = ninetyPercentOfScreen,
            minHeight = 40.dp,
            maxHeight = Dp.Infinity
        )

    Surface(
        shape = bubbleShape,
        tonalElevation = 16.dp,
        shadowElevation = 0.8.dp,
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.padding(
            top = if (receivedMessageItem.lastMessageWas == LastMessageWas.SENT) {
                8.dp
            } else {
                0.dp
            }
        )
    ) {
        Box(
            modifier = sizedModifier.padding(
                    start = if (isLastMessageReceivedOne) {
                        8.dp
                    } else {
                        0.dp
                    }
                )
        ) {
            val rect1 = remember { mutableStateOf<Rect?>(null) }
            val rect2 = remember { mutableStateOf<Rect?>(null) }
            val messageStartPadding = if (isLastMessageReceivedOne) {
                14.dp
            } else {
                22.dp
            }
            Text(
                text = receivedMessageItem.message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(bottom = messageBottomPadding)
                    .padding(start = messageStartPadding, top = 8.dp, end = 8.dp, bottom = 8.dp)
                    .onGloballyPositioned(onGloballyPositioned = {
                        rect1.value = it.boundsInWindow()
                    })
            )


            Text(
                text = receivedMessageItem.time,
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
}

@Composable
fun SentMessageBubble(sentMessageItem: SentMessageItem, modifier: Modifier = Modifier) {
    var messageBottomPadding by remember { mutableStateOf(0.dp) }
    val ninetyPercentOfScreen = (LocalConfiguration.current.screenWidthDp * 0.9f).dp
    val isLastMessageSentOne = sentMessageItem.lastMessageWas == LastMessageWas.SENT
    val bubbleShape = if (isLastMessageSentOne) {
        RoundedCornerShape(8.dp)
    } else {
        SentMessageShape()
    }
    val sizedModifier = modifier.requiredSizeIn(
            minWidth = 120.dp,
            maxWidth = ninetyPercentOfScreen,
            minHeight = 40.dp,
            maxHeight = Dp.Infinity
        )

    Row(
        Modifier
            .fillMaxWidth()
            .padding(
                top = if (sentMessageItem.lastMessageWas == LastMessageWas.RECEIVED) {
                    8.dp
                } else {
                    0.dp
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
                end = if (isLastMessageSentOne) {
                    8.dp
                } else {
                    0.dp
                }
            )
        ) {
            Box(
                modifier = sizedModifier
            ) {
                val rect1 = remember { mutableStateOf<Rect?>(null) }
                val rect2 = remember { mutableStateOf<Rect?>(null) }
                val messageEndPadding = if (isLastMessageSentOne) {
                    14.dp
                } else {
                    22.dp
                }
                Text(
                    text = sentMessageItem.message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = messageBottomPadding)
                        .padding(end = messageEndPadding, top = 8.dp, start = 8.dp, bottom = 8.dp)
                        .onGloballyPositioned(onGloballyPositioned = {
                            rect1.value = it.boundsInWindow()
                        })
                )


                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,

                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = if(isLastMessageSentOne){
                            8.dp
                        } else {
                            16.dp
                        }, bottom = 4.dp)
                        .onGloballyPositioned(onGloballyPositioned = {
                            rect2.value = it.boundsInWindow()
                        })
                ) {
                    Text(
                        text = sentMessageItem.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Icon(
                        painter = painterResource(
                            when(sentMessageItem.sendStatus){
                                SendStatus.LOADING -> R.drawable.loading_icon
                                SendStatus.ONE_TICK -> R.drawable.outline_check_24
                                SendStatus.TWO_TICKS -> R.drawable.double_tick_icon
                            }
                        ),
                        modifier = Modifier.size(18.dp),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
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
}

@Preview(uiMode = 33)
@Composable
private fun SentMessageShapePrev() {
    FakeWhatsAppTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)
            ) {
                ReceivedMessageBubble(
                    ReceivedMessageItem(
                        lastMessageWas = LastMessageWas.None,
                        message = "I will think of something to make you spill",//"Aah bummer😕. Sawa incase uipate just let me know, thanks. kelly",
                        time = "14:30"
                    )
                )
                SentMessageBubble(
                    SentMessageItem(
                        lastMessageWas = LastMessageWas.None,
                        message = "Hii, good afternoon. Did we get any luck in finding sekiro?",
                        time = "14:29",
                        sendStatus = SendStatus.TWO_TICKS
                    )
                )
                SentMessageBubble(
                    SentMessageItem(
                        message = "Eeh😂",
                        lastMessageWas = LastMessageWas.SENT,
                        time = "12:48",
                        sendStatus = SendStatus.ONE_TICK
                    )
                )
                SentMessageBubble(
                    SentMessageItem(
                        message = "Sawa",
                        lastMessageWas = LastMessageWas.SENT,
                        time = "12:49",
                        sendStatus = SendStatus.LOADING
                    )
                )
                MessageTextField(
                    messageValue = "",
                    onMessageChange = {},
                )
            }
        }
    }
}