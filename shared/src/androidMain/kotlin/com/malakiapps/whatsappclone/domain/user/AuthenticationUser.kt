package com.malakiapps.whatsappclone.domain.user

import android.net.Uri

actual class AuthenticationUser(
    actual val name: String,
    actual val email: String?,
    actual val type: UserType,
    val initialImage: Uri?,
)