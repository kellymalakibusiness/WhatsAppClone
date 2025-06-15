package com.malakiapps.whatsappclone.android.presentation.compose.screens.updates_screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.presentation.compose.common.TopAppBarButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesTopAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        modifier = modifier.shadow(
            elevation = 1.dp,
            spotColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Updates",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                TopAppBarButton(
                    icon = R.drawable.topappbar_search,
                    onClick = {  }
                )
                TopAppBarButton(
                    icon = R.drawable.topappbar_settings,
                    onClick = {  }
                )
            }
        }
    )
}

@Preview
@Composable
private fun UpdatesTopAppBarPrev() {
    FakeWhatsAppTheme {
        UpdatesTopAppBar()
    }
}