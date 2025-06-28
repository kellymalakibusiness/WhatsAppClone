package com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen

import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Profile

data class UserDetailsInfo(
    val image: Image?,
    val name: Name,
    val email: Email,
    val about: About
){
    companion object {
        fun Profile.toUserDetailsInfo(): UserDetailsInfo {
            return UserDetailsInfo(
                image = image,
                name = name,
                email = email,
                about = about,
            )
        }
    }
}
