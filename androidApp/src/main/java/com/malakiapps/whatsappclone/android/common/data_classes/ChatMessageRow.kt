package com.malakiapps.whatsappclone.android.common.data_classes

import androidx.annotation.DrawableRes

data class ChatMessageRow(
    val image: Int,
    val name: String,
    val lastMessage: String,
    val newMessagesCount: Int?,
    val time: String
)
