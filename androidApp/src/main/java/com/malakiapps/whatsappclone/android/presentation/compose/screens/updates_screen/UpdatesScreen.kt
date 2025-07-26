package com.malakiapps.whatsappclone.android.presentation.compose.screens.updates_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.presentation.compose.screens.chat_screen.MessageRow
import com.malakiapps.whatsappclone.android.presentation.compose.screens.dashboard.DashboardScreenType
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.messages.SendStatus
import com.malakiapps.whatsappclone.domain.screens.ChatsScreenConversationRow
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.TimeValue

@Composable
fun UpdatesScreen(
    onDashboardScreenChange: (DashboardScreenType) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .matchParentSize()
                .verticalScroll(
                    state = rememberScrollState()
                )
        ) {
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    StatusTab(
                        statusCard = AddStatusCard(
                            userProfile = R.drawable.kevin_durant
                        )
                    )
                }
                items(
                    items = (1..12).map { it }
                ) {
                    StatusTab(
                        statusCard = UserStatusCard(
                            userProfile = R.drawable.kevin_durant,
                            statusImage = R.drawable.kevin_durant,
                            isViewed = it % 3 == 0,
                            name = "Kevin Durant"
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Channels",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {}
                ) {
                    Text(
                        text = "Explore",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.keyboard_arrow_right),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }

            Column {
                listOf(
                    ChatsScreenConversationRow(
                        email = Email(""),
                        image = null,
                        name = Name("FakeWhatsApp"),
                        lastMessage = MessageValue("No support for channels yet. Maybe in the future"),
                        newMessagesCount = 1,
                        time = TimeValue("In the future"),
                        isMyMessage = true,
                        sendStatus = SendStatus.TWO_TICKS,
                    )
                ).forEach { channel ->
                    MessageRow(
                        row = channel,
                        onClick = {}
                    )
                }
            }

            Text(
                text = "Find channels to follow",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
            )

            Column {
                generateTempChannelsToFollow().forEach {
                    ChannelToFollowRow(
                        channelToFollow = it
                    )
                }
            }

            OutlinedButton(
                onClick = {},
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 100.dp
                ),
            ) {
                Text(
                    text = "Explore more",
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
        //The settings dialog
    }
}

@Preview(uiMode = 33)
@Composable
private fun UpdatesScreenPreview() {
    FakeWhatsAppTheme {
        UpdatesScreen(
            onDashboardScreenChange = {}
        )
    }
}

private fun generateTempChannelsToFollow(): List<ChannelToFollow> {
    return (0 until 4).map {
        ChannelToFollow(
            image = R.drawable.kevin_durant,
            name = "WCB Wasafi",
            followers = "837k",
            isVerified = it % 3 == 0
        )
    }
}