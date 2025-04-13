package com.malakiapps.whatsappclone.android.screens.calls_screen

import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malakiapps.whatsappclone.android.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.common.shared_composables.TopAppBarButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsTopAppBar(modifier: Modifier = Modifier) {
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
                        text = "Calls",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TopAppBarButton(
                        icon = R.drawable.topappbar_search,
                        onClick = { }
                    )
                    TopAppBarButton(
                        icon = R.drawable.topappbar_settings,
                        onClick = { }
                    )
                }
            }
        )
    }
}

@Preview
@Composable
private fun CallsTopAppBarPrev() {
    FakeWhatsAppTheme {
        CallsTopAppBar()
    }
}