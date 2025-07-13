package com.malakiapps.whatsappclone.android.presentation.compose.screens.new_chat_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.presentation.compose.common.NoProfileImage
import com.malakiapps.whatsappclone.android.presentation.compose.common.base64ToUri
import com.malakiapps.whatsappclone.android.presentation.compose.common.shimmerEffect
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name

@Composable
fun ContactRow(image: Image?, name: Name, about: About, onClick: () -> Unit, modifier: Modifier = Modifier, showAdd: Boolean = false, onAddClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable{
                onClick()
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        if (image != null) {
            AsyncImage(
                model = image.base64ToUri().value,
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
                text = name.value,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = about.value,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.widthIn(max = 200.dp)
            )
        }

        if(showAdd){
            Spacer(Modifier.weight(1f))
            Text(
                text = "Add",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onAddClick()
                    }
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun LoadingContactRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .shimmerEffect(CircleShape)
                .size(50.dp)
        )

        Column(
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .shimmerEffect(RectangleShape)
                    .fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .shimmerEffect(RectangleShape)
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun ContactRowPrev() {
    FakeWhatsAppTheme {
        Surface {
            Column {
                ContactRow(
                    image = null,
                    name = Name("Batman"),
                    about = About("Hi, I'm batman"),
                    onClick = {}
                )
                LoadingContactRow()
                ContactRow(
                    image = null,
                    name = Name("Second"),
                    about = About("Hi, I'm second"),
                    onClick = {},
                    showAdd = true
                )
            }
        }
    }
}