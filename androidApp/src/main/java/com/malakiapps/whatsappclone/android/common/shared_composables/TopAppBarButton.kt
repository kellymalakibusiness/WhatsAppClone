package com.malakiapps.whatsappclone.android.common.shared_composables

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

@Composable
fun TopAppBarButton(@DrawableRes icon: Int, onClick: () -> Unit) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null
        )
    }
}