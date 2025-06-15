package com.malakiapps.whatsappclone.android.domain.utils

import android.net.Uri

sealed interface UserUpdateType

data class UserNameUpdate(
    val value: String
): UserUpdateType

data class UserAboutUpdate(
    val value: String
): UserUpdateType

data class UserImageUpdate(
    val image: Uri
): UserUpdateType