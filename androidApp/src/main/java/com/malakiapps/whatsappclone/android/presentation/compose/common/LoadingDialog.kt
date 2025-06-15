package com.malakiapps.whatsappclone.android.presentation.compose.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme

@Composable
fun LoadingDialog(modifier: Modifier = Modifier) {
    Dialog(
        onDismissRequest = {},
    ) {
        Surface(
            modifier = modifier.fillMaxWidth().clip(MaterialTheme.shapes.small),
            shadowElevation = 2.dp,
            tonalElevation = 16.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    "Please wait a moment...",
                    style = MaterialTheme.typography.bodySmall
                    )
            }
        }
    }
}

@Preview
@Composable
private fun LoadingDialogPrev() {
    FakeWhatsAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoadingDialog()
        }
    }
}