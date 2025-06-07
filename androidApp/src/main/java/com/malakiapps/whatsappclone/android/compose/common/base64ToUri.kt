package com.malakiapps.whatsappclone.android.compose.common

fun String.base64ToUri(): String {
    return "data:image/png;base64,$this"
}