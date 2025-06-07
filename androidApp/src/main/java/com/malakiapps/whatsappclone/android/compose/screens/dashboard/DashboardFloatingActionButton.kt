package com.malakiapps.whatsappclone.android.compose.screens.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.R

@Composable
fun DashboardFloatingActionButton(
    dashboardScreenType: DashboardScreenType,
    modifier: Modifier = Modifier,
    onButton1Press: () -> Unit = {},
    onButton2Press: () -> Unit = {}
) {
    if (dashboardScreenType != DashboardScreenType.COMMUNITIES) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = dashboardScreenType == DashboardScreenType.UPDATES,
                enter = slideInVertically(
                    initialOffsetY = { it }
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it }
                ),
            ) {
                IconButton(
                    onClick = onButton2Press,
                    modifier = Modifier
                        .padding(bottom = 22.dp)
                        .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.large)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.onSecondary)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            IconButton(
                onClick = onButton1Press,
                modifier = modifier
                    .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.large)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(6.dp)

            ) {
                val icon = when (dashboardScreenType) {
                    DashboardScreenType.CHATS -> R.drawable.add_message
                    DashboardScreenType.UPDATES -> R.drawable.add_photo
                    DashboardScreenType.COMMUNITIES -> R.drawable.edit//This is never visible
                    DashboardScreenType.CALLS -> R.drawable.add_call
                }
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}