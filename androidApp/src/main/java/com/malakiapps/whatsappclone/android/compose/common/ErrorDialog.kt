package com.malakiapps.whatsappclone.android.compose.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.malakiapps.whatsappclone.android.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.domain.utils.ScreenError

@Composable
fun ErrorDialog(screenError: ScreenError, onDismissDialog: () -> Unit, modifier: Modifier = Modifier) {
    AlertDialog(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        onDismissRequest = onDismissDialog,
        confirmButton = {
            Text(
                screenError.dismissButton,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable {
                    onDismissDialog()
                }
            )
        },
        icon = {

        },
        title = {

        },
        text = {
            Text(
                screenError.message,
                style = MaterialTheme.typography.bodySmall
            )
        }
    )
}

@Preview
@Composable
private fun LoadingDialogPrev() {
    FakeWhatsAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ErrorDialog(
                screenError = ScreenError(
                    message = "An error occurred. Because I stand strong. And more I stand strong.",
                    dismissButton = "Close"
                ),
                onDismissDialog = {}
            )
        }
    }
}