package com.malakiapps.whatsappclone.android.presentation.compose.common

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.malakiapps.whatsappclone.android.R

@Composable
fun NoProfileImage(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.account_circle),
        contentDescription = "Add image icon",
        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
        modifier = modifier
    )
}