package com.malakiapps.whatsappclone.android.screens.dashboard

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.malakiapps.whatsappclone.android.R
import androidx.compose.ui.unit.dp

@Composable
fun FWhatsAppBottomAppBar(dashboardScreenType: DashboardScreenType, onScreenClick: (DashboardScreenType) -> Unit, modifier: Modifier = Modifier) {
    BottomAppBar(
        modifier = modifier.shadow(
            elevation = 5.dp,
            spotColor = MaterialTheme.colorScheme.onSurface
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,

    ) {

        FWhatsAppNavigationBarItem(
            isCurrentScreen = dashboardScreenType == DashboardScreenType.CHATS,
            fragment = DashboardScreenType.CHATS,
            selectedIcon = R.drawable.chat,
            unselectedIcon = R.drawable.chat_unselected,
            name = "Chats",
            onScreenClick = onScreenClick
        )

        FWhatsAppNavigationBarItem(
            isCurrentScreen = dashboardScreenType == DashboardScreenType.UPDATES,
            fragment = DashboardScreenType.UPDATES,
            selectedIcon = R.drawable.status_selected,
            unselectedIcon = R.drawable.status_unselected,
            name = "Updates",
            onScreenClick = onScreenClick
        )

        FWhatsAppNavigationBarItem(
            isCurrentScreen = dashboardScreenType == DashboardScreenType.COMMUNITIES,
            fragment = DashboardScreenType.COMMUNITIES,
            selectedIcon = R.drawable.group_selected,
            unselectedIcon = R.drawable.group_unselected,
            name = "Communities",
            onScreenClick = onScreenClick
        )

        FWhatsAppNavigationBarItem(
            isCurrentScreen = dashboardScreenType == DashboardScreenType.CALLS,
            fragment = DashboardScreenType.CALLS,
            selectedIcon = R.drawable.baseline_call_24,
            unselectedIcon = R.drawable.call_unselected,
            name = "Calls",
            onScreenClick = onScreenClick
        )
    }
}

@Composable
fun RowScope.FWhatsAppNavigationBarItem(
    isCurrentScreen: Boolean,
    fragment: DashboardScreenType,
    @DrawableRes selectedIcon: Int,
    @DrawableRes unselectedIcon: Int,
    name: String,
    onScreenClick: (DashboardScreenType) -> Unit,
) {
    NavigationBarItem(
        selected = isCurrentScreen,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
            indicatorColor = MaterialTheme.colorScheme.tertiaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onBackground
        ),
        onClick = {
            onScreenClick(fragment)
        },
        icon = {
            Icon(
                modifier = Modifier.height(22.dp),
                painter = painterResource(
                    if(isCurrentScreen){
                        selectedIcon//R.drawable.status_selected
                    } else {
                        unselectedIcon//R.drawable.status_unselected
                    }
                ),
                contentDescription = null
            )
        },
        label = {
            Text(
                name,
                fontWeight = if(isCurrentScreen){
                    FontWeight.SemiBold
                } else {
                    null
                }
            )
        }
    )
}