package com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen

import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.User

data class UserDetailsInfo(
    val image: Image?,
    val name: Name,
    val email: Email,
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
