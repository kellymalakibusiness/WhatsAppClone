package com.malakiapps.whatsappclone.android.presentation.compose.screens.new_chat_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.presentation.compose.common.NoProfileImage
import com.malakiapps.whatsappclone.android.presentation.compose.common.base64ToUri

@Composable
fun ContactRow(image: String?, name: String, about: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clickable{
                onClick()
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        if (image != null) {
            AsyncImage(
                model = image.base64ToUri(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        } else {
            NoProfileImage(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        }

        Column(
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 16.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = about,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.widthIn(max = 200.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ContactRowPrev() {
    FakeWhatsAppTheme {
        Surface {
            ContactRow(
                image = null,
                name = "Batman",
                about = "Hi, I'm batman",
                onClick = {}
            )
        }
    }
}