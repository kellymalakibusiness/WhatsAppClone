package com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.domain.messages.MessageId
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.screens.ConversationMessage
import com.malakiapps.whatsappclone.domain.screens.MessageCard
import com.malakiapps.whatsappclone.domain.screens.MessageType
import com.malakiapps.whatsappclone.domain.screens.TimeCard
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.TimeValue

@Composable
fun ConversationScreen(target: Profile?, messages: List<MessageCard>?, onBackPress: () -> Unit, onSendMessage: (String) -> Unit, onProfileClick: () -> Unit, modifier: Modifier = Modifier) {
    var messageText by remember {
        mutableStateOf("")
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            ConversationTopAppBar(
                profile  = target,
                selectedMessages = null,
                onProfileClick = onProfileClick,
                onNavigateBack = onBackPress,
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxSize()
        ){
            Image(
                painter = painterResource(R.drawable.conversation_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(paddingValues)
                    .imePadding()
            ){
                LazyColumn(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    //Messages go here
                    messages?.let { availableMessages ->
                        items(items = availableMessages) { card ->
                            when(card){
                                is ConversationMessage -> {
                                    if(card.messageType == MessageType.RECEIVED){
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
                                    TimeBubble(
                                        timeCard = card
                                    )
                                }
                            }
                        }
                    }
                }

                //Message input
                MessageTextField(
                    messageValue = messageText,
                    onMessageChange = { newValue ->
                        messageText = newValue
                    },
                    onRecordMessagePress = {},
                    onSendMessage = {
                        onSendMessage(messageText)
                        messageText = ""
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview//(uiMode = 33)
@Composable
private fun ConversationScreenPrev() {
    FakeWhatsAppTheme {
        ConversationScreen(
            messages = listOf(
                ConversationMessage(
                    messageId = MessageId(""),
                    message = MessageValue("banaa mi pia natafuta padi kama wajua store yoyote iko open enda ubuy ucon alafu unambie nilipe uje nayo"),
                    previousMessageType = MessageType.SENT,
                    messageType = MessageType.RECEIVED,
                    sendStatus = SendStatus.TWO_TICKS_READ,
                    time = TimeValue("12:45"),
                    isStartOfReply = true
                ),
                ConversationMessage(
                    messageId = MessageId(""),
                    message = MessageValue("Aah ata basi tutaenda na wewe majioni watakuwa wamefungua"),
                    messageType = MessageType.SENT,
                    previousMessageType = MessageType.SENT,
                    time = TimeValue("12:47"),
                    sendStatus = SendStatus.TWO_TICKS_READ,
                    isStartOfReply = true
                ),
                ConversationMessage(
                    messageId = MessageId(""),
                    message = MessageValue("Three four five six seven eight"),
                    messageType = MessageType.SENT,
                    previousMessageType = MessageType.RECEIVED,
                    time = TimeValue("12:47"),
                    sendStatus = SendStatus.TWO_TICKS_READ,
                    isStartOfReply = false
                ),
                ConversationMessage(
                    messageId = MessageId(""),
                    message = MessageValue( "Eeh😂"),
                    messageType = MessageType.RECEIVED,
                    previousMessageType = MessageType.None,
                    time = TimeValue("12:48"),
                    sendStatus = SendStatus.TWO_TICKS,
                    isStartOfReply = true,
                    isSelected = true
                )
            ),
            onBackPress = {},
            onProfileClick = {},
            onSendMessage = {},
            target = Profile(
                name = Name("Kevin Durant"),
                about = About(""),
                image = null,
                email = Email("")
            )
        )
    }
}