package com.malakiapps.whatsappclone.android.presentation.compose.screens.chat_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.compose.common.NoProfileImage
import com.malakiapps.whatsappclone.android.presentation.compose.common.base64ToUri
import com.malakiapps.whatsappclone.domain.messages.MessageValue
import com.malakiapps.whatsappclone.domain.screens.ChatsScreenConversationRow
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.TimeValue

@Composable
fun MessageRow(row: ChatsScreenConversationRow, onClick: (Email) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clickable{
            onClick(row.email)
        }.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {

        row.image?.let {
            AsyncImage(
                model = it.base64ToUri().value,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        } ?: run {
            NoProfileImage(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        }

        Column(
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 16.dp)
        ) {
            Row {
                val color = if(row.newMessagesCount == null){
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.tertiary
                }
                Text(
                    text = row.name.value,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = row.time.value,
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }

            Row {
                Text(
                    text = row.lastMessage?.value ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.widthIn(max = 200.dp)
                )

                if(row.newMessagesCount != null){
                    Spacer(Modifier.weight(1f))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .size(16.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    ){
                        Text(
                            text = row.newMessagesCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.Top,
        ) {

        }
    }
}

@Preview
@Composable
private fun MessageRowPrev() {
    FakeWhatsAppTheme {
        Surface {
            MessageRow(
                row = ChatsScreenConversationRow(
                    email = Email(""),
                    image = null,
                    name = Name("Kevin Durant"),
                    lastMessage = MessageValue("I'm Kevin Durant. You know who I am."),
                    time = TimeValue("Yesterday"),
                    newMessagesCount = 1
                ),
                onClick = {}
            )
        }
    }
}