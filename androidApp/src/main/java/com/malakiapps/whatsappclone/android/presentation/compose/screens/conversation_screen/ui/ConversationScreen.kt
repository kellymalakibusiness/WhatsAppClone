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
import com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.data.LastMessageWas
import com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.data.MessageItem
import com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.data.ReceivedMessageItem
import com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.data.SendStatus
import com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.data.SentMessageItem
import com.malakiapps.whatsappclone.domain.messages.SendStatus

@Composable
fun ConversationScreen(messageItems: List<MessageItem>, onBackPress: () -> Unit, modifier: Modifier = Modifier) {
    var messageText by remember {
        mutableStateOf("")
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            ConversationTopAppBar(
                image = R.drawable.kevin_durant,
                label = "Kevin Durant",
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
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    //Messages go here
                    items(items = messageItems) { message ->
                        when (message){
                            is ReceivedMessageItem -> {
                                ReceivedMessageBubble(
                                    receivedMessageItem = message,
                                )
                            }
                            is SentMessageItem -> {
                                SentMessageBubble(
                                    sentMessageItem = message
                                )
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
            messageItems = listOf(
                /*SentMessageItem(
                    message = "Hii, good afternoon. Did we get any luck in finding sekiro",
                    time = "14:29",
                    lastMessageWas = LastMessageWas.None,
                    sendStatus = SendStatus.TWO_TICKS
                ),
                ReceivedMessageItem(
                    message = "hey",
                    time = "14:30",
                    lastMessageWas = LastMessageWas.SENT
                ),
                ReceivedMessageItem(
                    message = "i have been posting everyday but not yet",
                    time = "14:30",
                    lastMessageWas = LastMessageWas.RECEIVED
                ),*/
                ReceivedMessageItem(
                    message = "banaa mi pia natafuta padi kama wajua store yoyote iko open enda ubuy ucon alafu unambie nilipe uje nayo",
                    lastMessageWas = LastMessageWas.None,
                    time = "12:45"
                ),
                SentMessageItem(
                    message = "Aah ata basi tutaenda na wewe majioni watakuwa wamefungua",
                    lastMessageWas = LastMessageWas.RECEIVED,
                    time = "12:47",
                    sendStatus = SendStatus.TWO_TICKS
                ),
                SentMessageItem(
                    message = "Mi pia nataka kubuy pesticide",
                    lastMessageWas = LastMessageWas.SENT,
                    time = "12:47",
                    sendStatus = SendStatus.TWO_TICKS
                ),
                SentMessageItem(
                    message = "Eeh😂",
                    lastMessageWas = LastMessageWas.SENT,
                    time = "12:48",
                    sendStatus = SendStatus.TWO_TICKS
                )
            ),
            onBackPress = {}
        )
    }
}