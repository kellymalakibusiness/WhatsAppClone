package com.malakiapps.whatsappclone.android.presentation.compose.screens.calls_screen

data class CallRow(
    val name: String,
    val image: Int,
    val date: String,
    val callType: CallType
)

enum class CallType{
    Missed,
    Received
}