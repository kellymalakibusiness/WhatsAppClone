package com.malakiapps.whatsappclone.android.compose.screens.dashboard

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.compose.common.TopAppBarButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopAppBar(dashboardScreenType: DashboardScreenType, modifier: Modifier = Modifier, onSearchPress: () -> Unit = {}, onCameraPress: () -> Unit= {}, onSettingsPress: () -> Unit) {

    var settingsDialogExpanded by remember { mutableStateOf(false) }
    val isCameraVisible = dashboardScreenType == DashboardScreenType.CHATS
    val isSearchVisible = dashboardScreenType != DashboardScreenType.COMMUNITIES

    val isChatScreen = dashboardScreenType == DashboardScreenType.CHATS

    TopAppBar(
        modifier = modifier.shadow(
            elevation = 1.dp,
            spotColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Text(
                text = dashboardScreenType.getScreenTitle(),
                color = if(isChatScreen) MaterialTheme.colorScheme.tertiary else Color.Unspecified,
                fontWeight = if(isChatScreen) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 24.sp
            )
        },
        actions = {
            if(isCameraVisible){
                TopAppBarButton(
                    icon = R.drawable.topappbar_camera,
                    onClick = onCameraPress
                )
            }

            if (isSearchVisible){
                TopAppBarButton(
                    icon = R.drawable.topappbar_search,
                    onClick = onSearchPress
                )
            }

            TopAppBarButton(
                icon = R.drawable.topappbar_settings,
                onClick = {
                    settingsDialogExpanded = true
                }
            )

                DropdownMenu(
                    expanded = settingsDialogExpanded,
                    onDismissRequest = {
                        settingsDialogExpanded = false
                    }
                ) {
                    when(dashboardScreenType){
                        DashboardScreenType.CHATS -> {
                            listOf(
                                Pair("New group"){},
                                Pair("Starred messages"){},
                                Pair("Read all"){},
                                Pair("Settings", onSettingsPress)
                            ).forEach {
                                it.ToDropdownMenuItem()
                            }
                        }
                        DashboardScreenType.UPDATES -> {
                            listOf(
                                Pair("Explore channels"){},
                                Pair("Create channel"){},
                                Pair("Status privacy"){},
                                Pair("Settings", onSettingsPress)
                            ).forEach {
                                it.ToDropdownMenuItem()
                            }
                        }
                        DashboardScreenType.COMMUNITIES -> {
                            Pair("Settings", onSettingsPress).ToDropdownMenuItem()
                        }
                        DashboardScreenType.CALLS -> {
                            listOf(
                                Pair("Clear call log"){},
                                Pair("Settings", onSettingsPress)
                            ).forEach {
                                it.ToDropdownMenuItem()
                            }
                        }
                    }
                }
        }
    )
}

private fun DashboardScreenType.getScreenTitle(): String {
    return when(this){
        DashboardScreenType.CHATS -> "FakeWhatsApp"
        DashboardScreenType.UPDATES -> "Updates"
        DashboardScreenType.COMMUNITIES -> "Communities"
        DashboardScreenType.CALLS -> "Calls"
    }
}

@Composable
private fun Pair<String, () -> Unit>.ToDropdownMenuItem() {
    DropdownMenuItem(
        text = {
            Text(this.first)
        },
        onClick = this.second
    )
}