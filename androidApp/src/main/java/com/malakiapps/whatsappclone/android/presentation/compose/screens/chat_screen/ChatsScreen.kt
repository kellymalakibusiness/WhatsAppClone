package com.malakiapps.whatsappclone.android.presentation.compose.screens.chat_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.domain.data_classes.ChatMessageRow

@Composable
fun ChatsScreen(
    onArchivedClick: () -> Unit,
    onMessageFilter: (MessageFilteringOption) -> Unit, modifier: Modifier = Modifier
) {

    var messageFilteringOption by rememberSaveable(stateSaver = MessageFilterSaver) {
        mutableStateOf(MessageFilteringOption.ALL)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
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
            item {
                ArchivedRow(
                    onClick = onArchivedClick,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(
                items = generateTempMessages(15)
            ) { message ->
                MessageRow(
                    row = message
                )
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
                onMessageFilter = {},
                onArchivedClick = {},
            )
        }
    }
}

fun generateTempMessages(size: Int): List<ChatMessageRow> {
    return (0 until size).map {
        ChatMessageRow(
            image = 2,
            name = "Kevin Durant",
            lastMessage = "I'm Kevin durant. You know who I am",
            newMessagesCount = if (it % 2 == 0) {
                1
            } else {
                null
            },
            time = "10:20"
        )
    }
}