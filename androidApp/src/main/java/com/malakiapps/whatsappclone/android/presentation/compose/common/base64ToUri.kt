package com.malakiapps.whatsappclone.android.presentation.compose.common

import com.malakiapps.whatsappclone.domain.user.Image

fun Image.base64ToUri(): Image {
    return Image("data:image/png;base64,${this.value}")
}