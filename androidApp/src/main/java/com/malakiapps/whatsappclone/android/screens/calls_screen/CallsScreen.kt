package com.malakiapps.whatsappclone.android.screens.calls_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.screens.dashboard.DashboardScreenType
import com.malakiapps.whatsappclone.android.screens.dashboard.FWhatsAppBottomAppBar

@Composable
fun CallsScreen(calls: List<CallRow>, onDashboardScreenChange: (DashboardScreenType) -> Unit, onAddCall: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CallsTopAppBar()
        },
        bottomBar = {
            FWhatsAppBottomAppBar(
                dashboardScreenType = DashboardScreenType.CALLS,
                onScreenClick = { dashboardFragment ->
                    if(dashboardFragment != DashboardScreenType.CALLS){
                        onDashboardScreenChange(dashboardFragment)
                    }
                }
            )
        },
        floatingActionButton = {
            IconButton(
                onClick = onAddCall,
                modifier = Modifier
                    .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.large)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(6.dp)

            ) {
                Icon(
                    painter = painterResource(R.drawable.add_call),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                AddFavouriteRow()
            }

            items(
                items = calls,
            ){ callRow ->
                Row(
                    modifier = Modifier.clickable {  }.padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(callRow.image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(60.dp)
                            .clip(RoundedCornerShape(30.dp))
                    )

                    Column {
                        Text(
                            text = callRow.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = when(callRow.callType){
                                CallType.Missed -> MaterialTheme.colorScheme.error
                                CallType.Received -> MaterialTheme.colorScheme.secondary
                            }
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = when(callRow.callType){
                                    CallType.Missed -> painterResource(R.drawable.arrow_down)
                                    CallType.Received -> painterResource(R.drawable.received_call)
                                },
                                contentDescription = null,
                                tint = when(callRow.callType){
                                    CallType.Missed -> MaterialTheme.colorScheme.error
                                    CallType.Received -> MaterialTheme.colorScheme.onTertiary
                                },
                                modifier = Modifier.size(12.dp)
                            )

                            Text(
                                text = callRow.date,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))

                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.call_unselected),
                            contentDescription = null
                        )
                    }

                }
            }
        }
    }
}

@Preview
@Composable
private fun CallsScreenPrev() {
    FakeWhatsAppTheme {
        CallsScreen(
            onDashboardScreenChange = {},
            onAddCall = {},
            calls = (0..3).map {
                CallRow(
                    name = "User 345",
                    image = R.drawable.kevin_durant,
                    date = "March 17, 11:07",
                    callType = CallType.entries.random()
                )
            }
        )
    }
}

@Preview(uiMode = 33)
@Composable
private fun CallsScreenPrev2() {
    FakeWhatsAppTheme {
        CallsScreen(
            onDashboardScreenChange = {},
            onAddCall = {},
            calls = (0..3).map {
                CallRow(
                    name = "User 123",
                    image = R.drawable.kevin_durant,
                    date = "March 17, 11:07",
                    callType = CallType.entries.random()
                )
            }
        )
    }
}