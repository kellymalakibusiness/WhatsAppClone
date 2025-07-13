package com.malakiapps.whatsappclone.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MessageEntity(
    @PrimaryKey(autoGenerate = false)
    val timeId: String,

)
