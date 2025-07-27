package com.malakiapps.whatsappclone.android.presentation.compose.screens.chat_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.presentation.compose.screens.new_chat_screen.LoadingContactRow
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.screens.ChatsScreenConversationRow
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.TimeValue

@Composable
fun ChatsScreen(
    conversations: List<ChatsScreenConversationRow>?,
    onArchivedClick: () -> Unit,
    onMessageSelect: (Email) -> Unit,
    onMessageFilter: (MessageFilteringOption) -> Unit, modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var isFilterVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var messageFilteringOption by rememberSaveable(stateSaver = MessageFilterSaver) {
        mutableStateOf(MessageFilteringOption.ALL)
    }
    var lastOffset by remember { mutableIntStateOf(0) }
    val scrollingOnTop by remember {
        derivedStateOf {
            isFilterVisible || listState.firstVisibleItemIndex == 0 && listState.isScrollInProgress
        }
    }
    var initialScrollHappened by remember { mutableStateOf(false) }

    //Detect when the user scrolls on top to show the filter row
    LaunchedEffect(scrollingOnTop) {
        val value = listState.firstVisibleItemScrollOffset
        if (value == lastOffset && initialScrollHappened) {
            isFilterVisible = true
        } else {
            initialScrollHappened = true
        }
        lastOffset = value
    }
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        if(conversations?.isEmpty() == true){
            Text(
                text = "No Chats. Press the add icon to start a new chat",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (isFilterVisible) {
                    item {
                        MessageFilteringRow(
                            activeOption = messageFilteringOption,
                            onFilterSelect = { messageFilter ->
                                onMessageFilter(messageFilter)
                                messageFilteringOption = messageFilter
                            },
                            modifier = Modifier.padding(
                                top = 32.dp,
                                bottom = 8.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        )
                    }
                }

                conversations?.let {
                    item {
                        ArchivedRow(
                            onClick = onArchivedClick,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(
                        items = conversations,
                        key = { it.email.value + (it.lastMessage?.value ?: "") }
                    ) { conversation ->
                        MessageRow(
                            row = conversation,
                            onClick = onMessageSelect
                        )
                    }
                } ?: run {
                    items(count = 10) {
                        LoadingContactRow()
                    }
                }
            }
        }
    }
}

object MessageFilterSaver : Saver<MessageFilteringOption, String> {
    override fun restore(value: String): MessageFilteringOption? {
        return MessageFilteringOption.entries.find { it.name == value }
    }

    override fun SaverScope.save(value: MessageFilteringOption): String? {
        return value.name
    }

}

@Preview//(uiMode = 33)
@Composable
private fun ChatScreenPreview() {
    FakeWhatsAppTheme {
        Surface {
            ChatsScreen(
                conversations = listOf(
                    ChatsScreenConversationRow(
                        image = null,
                        name = Name("Kelly"),
                        lastMessage = MessageValue("Bello"),
                        newMessagesCount = 0,
                        time = TimeValue("Yesterday"),
                        email = Email(""),
                        isMyMessage = true,
                        sendStatus = SendStatus.TWO_TICKS_READ
                    )
                ),
                onMessageFilter = {},
                onArchivedClick = {},
                onMessageSelect = {}
            )
        }
    }
}