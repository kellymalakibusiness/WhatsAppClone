package com.malakiapps.whatsappclone.android.compose.screens.settings_screen

import com.malakiapps.whatsappclone.domain.user.User

data class UserDetailsInfo(
    val image: String?,
    val name: String,
    val email: String,
    val about: String
){
    companion object {
        fun User.toUserDetailsInfo(): UserDetailsInfo {
            return UserDetailsInfo(
                image = image,
                name = name,
                email = email,
                about = about,
            )
        }
    }
}
