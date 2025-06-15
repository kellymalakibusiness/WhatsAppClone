package com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.malakiapps.whatsappclone.android.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malakiapps.whatsappclone.android.presentation.compose.common.TopAppBarButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        TopAppBar(
            modifier = modifier,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TopAppBarButton(
                        icon = R.drawable.topappbar_search,
                        onClick = { }
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                       onNavigateBack()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back arrow"
                    )
                }
            }
        )
    }
}