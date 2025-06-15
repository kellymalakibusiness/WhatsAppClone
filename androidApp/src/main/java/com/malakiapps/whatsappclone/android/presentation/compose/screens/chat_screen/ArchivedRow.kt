package com.malakiapps.whatsappclone.android.presentation.compose.screens.chat_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.R

@Composable
fun ArchivedRow(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable{
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.archive),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
        )

        Text(
            text = "Archived",
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )

    }
}

@Preview
@Composable
private fun ArchivedRowPev() {
    FakeWhatsAppTheme {
        Surface {
            ArchivedRow(
                onClick = {}
            )
        }
    }
}