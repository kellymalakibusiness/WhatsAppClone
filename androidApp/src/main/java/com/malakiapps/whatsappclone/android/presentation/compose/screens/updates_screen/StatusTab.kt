package com.malakiapps.whatsappclone.android.presentation.compose.screens.updates_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R

@Composable
fun StatusTab(statusCard: StatusCard, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(width = 85.dp, height = 140.dp).clip(RoundedCornerShape(12.dp))
    ){
        when(statusCard){
            is AddStatusCard -> {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.onSecondary)
                )
                AddStatusLayer()
            }
            is UserStatusCard -> {
                Image(
                    painter = painterResource(statusCard.statusImage),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .blur(
                            radiusX = 2.dp,
                            radiusY = 2.dp,
                            edgeTreatment = BlurredEdgeTreatment.Rectangle
                        )
                )
                UserStatus(userStatusCard = statusCard)
            }
        }
    }
}

@Composable
fun BoxScope.AddStatusLayer(modifier: Modifier = Modifier) {
    Text(
        text = "Add status",
        fontSize = 12.sp,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 4.dp, bottom = 4.dp)
    )

    Box(
        modifier = Modifier.size(44.dp)
    ){
        Image(
            painter = painterResource(R.drawable.kevin_durant),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(horizontal = 2.dp, vertical = 2.dp)
                .padding(top = 2.dp, start = 2.dp)
                .matchParentSize()
                //.size(40.dp)
                .clip(RoundedCornerShape(20.dp))
        )

        Icon(
            painter = painterResource(R.drawable.add),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .size(15.dp)
                .clip(RoundedCornerShape(20.dp))
                .align(Alignment.BottomEnd)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun BoxScope.UserStatus(userStatusCard: UserStatusCard){
    Text(
        text = userStatusCard.name,
        fontSize = 12.sp,
        color = Color.White,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 4.dp, bottom = 4.dp)
    )

    Box(
        modifier = Modifier
            .padding(top = 4.dp, start = 4.dp)
            .border(
                width = 1.dp,
                color = if(userStatusCard.isViewed){
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.tertiary
                },
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Image(
            painter = painterResource(R.drawable.kevin_durant),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(3.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
        )
    }
}

@Preview
@Composable
private fun StatusTabPrev() {
    FakeWhatsAppTheme {
        Surface {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusTab(
                    statusCard = AddStatusCard(
                        userProfile = R.drawable.kevin_durant
                    )
                )
                StatusTab(
                    statusCard = UserStatusCard(
                        userProfile = R.drawable.kevin_durant,
                        statusImage = R.drawable.kevin_durant,
                        isViewed = false,
                        name = "Kevin Durant"
                    )
                )
                StatusTab(
                    statusCard = UserStatusCard(
                        userProfile = R.drawable.kevin_durant,
                        statusImage = R.drawable.kevin_durant,
                        isViewed = true,
                        name = "Kelly Malaki"
                    )
                )
            }
        }
    }
}