package com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.account_screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R

@Composable
fun AccountSettingsScreen(onNavigateBack: () -> Unit, onLogout: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AccountSettingsTopApBar(
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
            AccountSettingsOption(
                text = "Security notifications",
                icon = R.drawable.security,
                onClick = {},
            )
            AccountSettingsOption(
                text = "Passkeys",
                icon = R.drawable.person_badge_key_fill,
                onClick = {},
            )
            AccountSettingsOption(
                text = "Request account info",
                icon = R.drawable.outline_insert_drive_file_24,
                onClick = {},
            )
            AccountSettingsOption(
                text = "How to delete my account",
                icon = R.drawable.outline_info_24,
                onClick = {},
            )
            AccountSettingsOption(
                text = "Log out",
                icon = R.drawable.baseline_logout_24,
                onClick = onLogout,
                isWarningOption = true
            )
        }
    }
}

@Preview
@Composable
private fun AccountSettingsScreenPrev() {
    FakeWhatsAppTheme {
        AccountSettingsScreen(
            onLogout = {},
            onNavigateBack = {}
        )
    }
}

@Composable
private fun AccountSettingsOption(text: String, @DrawableRes icon: Int, onClick: () -> Unit, modifier: Modifier = Modifier, isWarningOption: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable{
                onClick()
            }
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = if(isWarningOption){
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.secondary
            },
            modifier = Modifier.padding(horizontal = 8.dp).size(24.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if(isWarningOption){
                MaterialTheme.colorScheme.error
            } else {
                Color.Unspecified
            }
        )
    }
}