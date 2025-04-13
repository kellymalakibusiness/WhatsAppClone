package com.malakiapps.whatsappclone.common.user

import android.net.Uri

actual class User(
    actual val id: String,
    actual val name: String,
    actual val email: String,
    val imageUri: Uri?
)