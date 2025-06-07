package com.malakiapps.whatsappclone.android.compose.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NoProfileImage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(60.dp)
            //.clip(RoundedCornerShape(30.dp))
            .shimmerEffect(MaterialTheme.shapes.large)
    )
}