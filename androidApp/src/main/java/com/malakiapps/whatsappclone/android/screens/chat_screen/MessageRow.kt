package com.malakiapps.whatsappclone.android.screens.chat_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import com.malakiapps.whatsappclone.android.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.common.data_classes.ChatMessageRow

@Composable
fun MessageRow(row: ChatMessageRow, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.kevin_durant),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(30.dp))
        )

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
                    text = row.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = row.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }

            Row {
                Text(
                    text = row.lastMessage,
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

        Column {


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
                row = ChatMessageRow(
                    image = R.drawable.kevin_durant,
                    name = "Kevin Durant",
                    lastMessage = "I'm Kevin Durant. You know who I am. Trust everybody but myself",
                    time = "Yesterday",
                    newMessagesCount = 1
                )
            )
        }
    }
}