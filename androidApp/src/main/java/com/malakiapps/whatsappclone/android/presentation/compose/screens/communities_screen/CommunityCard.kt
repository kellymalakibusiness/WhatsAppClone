package com.malakiapps.whatsappclone.android.presentation.compose.screens.communities_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R

@Composable
fun CommunityCard(
    communityName: String,
    communityImage: Int,
    groups: List<CommunityGroup>,
    onCommunityClick: () -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(communityImage),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .size(50.dp),
            )

            //The New community text
            Text(
                communityName,
                style = MaterialTheme.typography.titleSmall,
            )
        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = DividerDefaults.color.copy(alpha = 0.7f)
            )

        AnnouncementRow()
        //Groups
        groups.forEach { group ->
            CommunityGroupMessage(
                groupName = group.name,
                communityImage = group.image,
                lastMessage = group.lastMessage
            )
        }

        //View all button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onViewAllClick()
                }
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.keyboard_arrow_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                //modifier = Modifier.padding(horizontal = 8.dp)
            )

            Text(
                "View all",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }
}

@Composable
private fun AnnouncementRow(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 50.dp, height = 46.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.megaphone_fill),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }

        Column(verticalArrangement = Arrangement.Top, modifier = Modifier.align(Alignment.Top)) {
            Row {
                Text(
                    "Announcements",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "3/12/25",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = "~Africa's Talking Community is now a commmmmmmmmmm",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CommunityGroupMessage(groupName: String, communityImage: Int, lastMessage: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(communityImage),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .size(50.dp),
        )

        Column(verticalArrangement = Arrangement.Top, modifier = Modifier.align(Alignment.Top)) {
            Row(modifier = Modifier.padding(bottom = 6.dp)) {
                Text(
                    groupName,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.widthIn(max = 200.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "3/12/25",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = lastMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun CommunityCardPrev() {
    FakeWhatsAppTheme {
        Surface {
            CommunityCard(
                communityName = "AT Community BroadCast",
                communityImage = R.drawable.kevin_durant,
                groups = listOf(
                    CommunityGroup(
                        name = "Kevin Durant",
                        image = R.drawable.kevin_durant,
                        lastMessage = "~Kelvin: Bello, Long live Bananas!!"
                    )
                ),
                onCommunityClick = {},
                onViewAllClick = {}
            )
        }
    }
}