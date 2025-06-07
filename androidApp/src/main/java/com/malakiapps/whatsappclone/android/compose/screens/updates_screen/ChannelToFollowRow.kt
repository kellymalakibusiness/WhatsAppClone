package com.malakiapps.whatsappclone.android.compose.screens.updates_screen

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R

@Composable
fun ChannelToFollowRow(channelToFollow: ChannelToFollow, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Image(
            painter = painterResource(channelToFollow.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(30.dp))
        )

        Column(
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = channelToFollow.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                if(channelToFollow.isVerified){
                    Icon(
                        painter = painterResource(R.drawable.verified),
                        contentDescription = null,
                        tint = Color(0xFF0085f6),
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Text(
                text = "${channelToFollow.followers} followers",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.widthIn(max = 200.dp)
            )

        }
        Spacer(Modifier.weight(1f))

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ),
        ) {
            Text(
                text = "Follow"
            )
        }
    }
}

@Preview
@Composable
private fun ChannelToFollowRowPrev() {
    FakeWhatsAppTheme {
        Surface {
            ChannelToFollowRow(
                channelToFollow = ChannelToFollow(
                    image = R.drawable.kevin_durant,
                    name = "WCB Wasafi",
                    followers = "837k",
                    isVerified = true
                )
            )
        }
    }
}

data class ChannelToFollow(
    val image: Int,
    val name: String,
    val followers: String,
    val isVerified: Boolean
)