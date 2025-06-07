package com.malakiapps.whatsappclone.android.compose.screens.communities_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R

@Composable
fun NewCommunityCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .clickable {
                onClick()
            }
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
            Box {
                Box(
                    modifier = Modifier
                        .size(width = 50.dp, height = 46.dp)
                        .align(Alignment.Center)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color(0xFFd5d6d8)),
                    contentAlignment = Alignment.Center,
                ){
                    Icon(
                        painter = painterResource(R.drawable.group_selected),
                        contentDescription = null,
                        modifier = Modifier
                            .size(38.dp),
                        tint = Color.White
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .align(Alignment.BottomEnd)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            //The New community text
            Text(
                "New community",
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Preview
@Composable
private fun NewCommunityCardPrev() {
    FakeWhatsAppTheme {
        Surface {
            NewCommunityCard(
                onClick = {},
                modifier = Modifier.padding(vertical = 16.dp))
        }
    }
}

@Preview(name = "DarkMode", uiMode = 33)
@Composable
private fun NewCommunityCardPrev2() {
    FakeWhatsAppTheme {
        Surface {
            NewCommunityCard(
                onClick = {}
            )
        }
    }
}