package com.malakiapps.whatsappclone.android.screens.settings_screen

import android.accounts.Account
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.screens.dashboard.DashboardScreenType
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.screens.dashboard.FWhatsAppBottomAppBar

@Composable
fun SettingsScreen(userDetailsInfo: UserDetailsInfo, onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SettingsTopAppBar(
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(state = rememberScrollState())
        ) {
            UserDetailsRow(
                userDetailsInfo = userDetailsInfo
            )

            SettingsRowOption(
                icon = R.drawable.outline_key,
                name = "Account",
                description = "Security notifications, log out"
            )

            SettingsRowOption(
                icon = R.drawable.outline_lock_24,
                name = "Privacy",
                description = "Block contacts, disappearing messages"
            )

            SettingsRowOption(
                icon = R.drawable.outline_favorite_border_24,
                name = "Favourites",
                description = "Add, reorder, remove"
            )

            SettingsRowOption(
                icon = R.drawable.outline_message_24,
                name = "Chats",
                description = "Theme, wallpapers, chat history"
            )

            SettingsRowOption(
                icon = R.drawable.outline_notifications_24,
                name = "Notifications",
                description = "Message, group & call tones"
            )

            SettingsRowOption(
                icon = R.drawable.baseline_data_saver_off_24,
                name = "Storage and data",
                description = "Network usage, auto-download"
            )

            SettingsRowOption(
                icon = R.drawable.baseline_language_24,
                name = "App language",
                description = "English(device's language)"
            )

            SettingsRowOption(
                icon = R.drawable.outline_help_outline_24,
                name = "Help",
                description = "Help center, contact us, privacy policy"
            )

            SettingsRowOption(
                icon = R.drawable.outline_group_24,
                name = "Invite a friend",
                description = null
            )

            SettingsRowOption(
                icon = R.drawable.outline_security_update_good_24,
                name = "App updates",
                description = null
            )

            Text(
                text = "Also from Meta",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(16.dp)
            )

            SettingsRowOption(
                icon = R.drawable.instagram,
                name = "Open Instagram",
                description = null
            )

            SettingsRowOption(
                icon = R.drawable.facebook,
                name = "Open Facebook",
                description = null
            )

            SettingsRowOption(
                icon = R.drawable.threads,
                name = "Open Threads",
                description = null
            )

        }
    }
}

@Composable
private fun UserDetailsRow(userDetailsInfo: UserDetailsInfo, modifier: Modifier = Modifier) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(userDetailsInfo.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(30.dp))
            )

            //Name part
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = userDetailsInfo.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = userDetailsInfo.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = userDetailsInfo.about,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = DividerDefaults.color.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun SettingsRowOption(icon: Int, name: String, description: String?, onClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .clickable{
                onClick()
            }
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "Icon for $name",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 8.dp).size(24.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ){
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall
            )

            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPrev() {
    FakeWhatsAppTheme {
        SettingsScreen(
            userDetailsInfo = UserDetailsInfo(
                image = R.drawable.kevin_durant,
                name = "Malaki",
                email = "kellymalaki@gmail.com",
                about = "Win Some, Lose Some"
            ),
            onNavigateBack = {},
        )
    }
}