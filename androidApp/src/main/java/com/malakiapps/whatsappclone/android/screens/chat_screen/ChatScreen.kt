package com.malakiapps.whatsappclone.android.screens.chat_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.common.data_classes.ChatMessageRow
import com.malakiapps.whatsappclone.android.screens.dashboard.DashboardScreenType
import com.malakiapps.whatsappclone.android.screens.dashboard.FWhatsAppBottomAppBar

@Composable
fun ChatScreen(
    onTopAppBarCamera: () -> Unit,
    onTopAppBarSearch: () -> Unit,
    onTopAppBarMore: () -> Unit,
    onArchivedClick: () -> Unit,
    onAddMessageClick: () -> Unit,
    onDashboardScreenChange: (DashboardScreenType) -> Unit,
    onMessageFilter: (MessageFilteringOption) -> Unit, modifier: Modifier = Modifier) {

    var messageFilteringOption by rememberSaveable(stateSaver = MessageFilterSaver){
        mutableStateOf(MessageFilteringOption.ALL)
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            ChatTopAppBar(
                onCameraSelect = onTopAppBarCamera,
                onSearchSelect = onTopAppBarSearch,
                onMoreSelect = onTopAppBarMore
            )
        },
        bottomBar = {
            FWhatsAppBottomAppBar(
                dashboardScreenType = DashboardScreenType.CHATS,
                onScreenClick = { dashboardFragment ->
                    if(dashboardFragment != DashboardScreenType.CHATS){
                        onDashboardScreenChange(dashboardFragment)
                    }
                }
            )
        },
        floatingActionButton = {
            IconButton(
                onClick = onAddMessageClick,
                modifier = Modifier
                    .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.large)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(6.dp)

            ) {
                Icon(
                    painter = painterResource(R.drawable.add_message),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                item {
                    MessageFilteringRow(
                        activeOption = messageFilteringOption,
                        onFilterSelect = { messageFilter ->
                            onMessageFilter(messageFilter)
                            messageFilteringOption = messageFilter
                        },
                        modifier = Modifier.padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                    )
                }
                item {
                    ArchivedRow(
                        onClick = onArchivedClick,
                        modifier = Modifier.padding(bottom = 16.dp)
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
}

object MessageFilterSaver: Saver<MessageFilteringOption, String>{
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
        ChatScreen(
            onDashboardScreenChange = {},
            onTopAppBarCamera = {},
            onTopAppBarSearch = {},
            onTopAppBarMore = {},
            onMessageFilter = {},
            onArchivedClick = {},
            onAddMessageClick = {}
        )
    }
}

fun generateTempMessages(size: Int): List<ChatMessageRow>{
    return (0 until size).map {
        ChatMessageRow(
            image = 2,
            name = "Kevin Durant",
            lastMessage = "I'm Kevin durant. You know who I am",
            newMessagesCount = if(it%2 == 0){
                1
            } else{
                null
            },
            time = "10:20"
        )
    }
}