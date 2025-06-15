package com.malakiapps.whatsappclone.android.presentation.compose.screens.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.compose.screens.calls_screen.CallRow
import com.malakiapps.whatsappclone.android.presentation.compose.screens.calls_screen.CallType
import com.malakiapps.whatsappclone.android.presentation.compose.screens.calls_screen.CallsScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.chat_screen.ChatsScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.communities_screen.CommunitiesScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.communities_screen.CommunityGroup
import com.malakiapps.whatsappclone.android.presentation.compose.screens.communities_screen.CommunityItem
import com.malakiapps.whatsappclone.android.presentation.compose.screens.dashboard.DashboardScreenType.Companion.indexToDashboardScreenType
import com.malakiapps.whatsappclone.android.presentation.compose.screens.updates_screen.UpdatesScreen
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(openSettings: () -> Unit, onPrimaryFloatingButtonPress: (DashboardScreenType) -> Unit, onSecondaryFloatingButtonPress: (DashboardScreenType) -> Unit, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val pagerScrollState = rememberPagerState{ 4 }
    val currentScreen = rememberSaveable { mutableStateOf(pagerScrollState.currentPage.indexToDashboardScreenType()) }

    LaunchedEffect(pagerScrollState.isScrollInProgress) {
        if(!pagerScrollState.isScrollInProgress){
            currentScreen.value = pagerScrollState.currentPage.indexToDashboardScreenType()
        }
    }

    BackHandler(enabled = currentScreen.value != DashboardScreenType.CHATS) {
        coroutineScope.launch {
            pagerScrollState.animateScrollToPage(DashboardScreenType.CHATS.index)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            DashboardTopAppBar(
                dashboardScreenType = currentScreen.value,
                onSettingsPress = openSettings,
                onSearchPress = {},
                onCameraPress = {}
            )
        },
        bottomBar = {
            FWhatsAppBottomAppBar(
                dashboardScreenType = currentScreen.value,
                onScreenClick = { dashboardFragment ->
                    if(dashboardFragment != currentScreen.value){
                        currentScreen.value = dashboardFragment
                        coroutineScope.launch {
                            pagerScrollState.scrollToPage(dashboardFragment.index)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            DashboardFloatingActionButton(
                dashboardScreenType = currentScreen.value,
                onButton1Press = { dashboardScreenType ->
                    onPrimaryFloatingButtonPress(dashboardScreenType)
                },
                onButton2Press = { dashboardScreenType ->
                    onSecondaryFloatingButtonPress(dashboardScreenType)
                }
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            modifier = Modifier.padding(paddingValues),
            state = pagerScrollState,
            beyondViewportPageCount = 3
        ) { page ->
            when(page){
                0 -> {
                    ChatsScreen(
                        onArchivedClick = {},
                        onMessageFilter = {}
                    )
                }
                1 -> {
                   UpdatesScreen {  }
                }
                2 -> {
                    CommunitiesScreen(
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
                3 -> {
                    CallsScreen(
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
                else -> {
                    Text("If you seeing this, things are bad!!")
                }
            }
        }
    }
}

@Preview
@Composable
private fun DashBoardScreenPrev() {
    FakeWhatsAppTheme {
        DashboardScreen(
            {}, {}, {})
    }
}