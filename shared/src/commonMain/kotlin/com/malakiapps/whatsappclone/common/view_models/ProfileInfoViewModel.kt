package com.malakiapps.whatsappclone.common.view_models

import androidx.lifecycle.ViewModel
import com.malakiapps.whatsappclone.common.user.UserAuthenticationRepository

class ProfileInfoViewModel(
    val authenticationRepository: UserAuthenticationRepository
): ViewModel() {
}