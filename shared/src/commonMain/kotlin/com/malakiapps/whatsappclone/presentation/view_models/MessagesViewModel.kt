package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import kotlinx.coroutines.launch

class MessagesViewModel(

): ViewModel() {
    fun initializeMessages(authenticationUser: AuthenticationUser){
        viewModelScope.launch {

        }
    }
}