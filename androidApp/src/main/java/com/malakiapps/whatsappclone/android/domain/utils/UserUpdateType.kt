package com.malakiapps.whatsappclone.android.domain.utils

import android.net.Uri
import com.malakiapps.whatsappclone.domain.user.Name

sealed interface UserUpdateType

data class UserNameUpdate(
    val value: Name
): UserUpdateType

data class UserAboutUpdate(
    val value: String
): UserUpdateType

data class UserImageUpdate(
    val image: Uri
): UserUpdateType