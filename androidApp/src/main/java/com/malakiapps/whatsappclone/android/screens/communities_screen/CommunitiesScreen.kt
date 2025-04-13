package com.malakiapps.whatsappclone.android.screens.communities_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.screens.dashboard.DashboardScreenType
import com.malakiapps.whatsappclone.android.screens.dashboard.FWhatsAppBottomAppBar

@Composable
fun CommunitiesScreen(onDashboardScreenChange: (DashboardScreenType) -> Unit, communities: List<CommunityItem>, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CommunitiesTopAppBar()
        },
        bottomBar = {
            FWhatsAppBottomAppBar(
                dashboardScreenType = DashboardScreenType.COMMUNITIES,
                onScreenClick = { dashboardFragment ->
                    if(dashboardFragment != DashboardScreenType.COMMUNITIES){
                        onDashboardScreenChange(dashboardFragment)
                    }
                }
            )
        },

    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                NewCommunityCard(
                    onClick = {},
                )
            }

            items(
                items = communities
            ){ eachCommunity ->
                CommunityCard(
                    communityName = eachCommunity.name,
                    communityImage = eachCommunity.image,
                    groups = eachCommunity.groups,
                    onCommunityClick = {},
                    onViewAllClick = {}
                )
            }
        }
    }
}


@Preview
@Composable
private fun CommunitiesScreenPrev() {
    FakeWhatsAppTheme {
        CommunitiesScreen(
            onDashboardScreenChange = { _ -> },
            communities = listOf(
                CommunityItem(
                    name = "AT Community Broadcast",
                    image = R.drawable.kevin_durant,
                    groups = listOf(
                        CommunityGroup(
                            name = "AT Community",
                            image = R.drawable.kevin_durant,
                            lastMessage = "~User1: This message was deleted."
                        ),
                    )
                ),
                CommunityItem(
                    name = "DITA",
                    image = R.drawable.kevin_durant,
                    groups = listOf(
                        CommunityGroup(
                            name = "MISADITA",
                            image = R.drawable.kevin_durant,
                            lastMessage = "~User3: Another Message"
                        ),
                    )
                ),
                CommunityItem(
                    name = "Safari Computer Club",
                    image = R.drawable.kevin_durant,
                    groups = listOf(
                        CommunityGroup(
                            name = "Computing Systems and Hackathons",
                            image = R.drawable.kevin_durant,
                            lastMessage = "~User4: Joined using this groupd...."
                        ),
                    )
                ),
            )
        )
    }
}

@Preview(uiMode = 33)
@Composable
private fun CommunitiesScreenPrev2() {
    FakeWhatsAppTheme {
        CommunitiesScreen(
            onDashboardScreenChange = { _ -> },
            communities = listOf(
                CommunityItem(
                    name = "AT Community Broadcast",
                    image = R.drawable.kevin_durant,
                    groups = listOf(
                        CommunityGroup(
                            name = "AT Community",
                            image = R.drawable.kevin_durant,
                            lastMessage = "~User1: This message was deleted."
                        ),
                    )
                ),
                CommunityItem(
                    name = "DITA",
                    image = R.drawable.kevin_durant,
                    groups = listOf(
                        CommunityGroup(
                            name = "MISADITA",
                            image = R.drawable.kevin_durant,
                            lastMessage = "~User3: Another Message"
                        ),
                    )
                ),
                CommunityItem(
                    name = "Safari Computer Club",
                    image = R.drawable.kevin_durant,
                    groups = listOf(
                        CommunityGroup(
                            name = "Computing Systems and Hackathons",
                            image = R.drawable.kevin_durant,
                            lastMessage = "~User4: Joined using this groupd...."
                        ),
                    )
                ),
            )
        )
    }
}