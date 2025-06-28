package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.user.Profile
import kotlinx.coroutines.launch

class ContactsViewModel(

): ViewModel() {

    fun initializeContacts(profile: Profile){
        viewModelScope.launch {
            //Need to check if we already have the messages and if it's the correct user,
            //this method would be called several times
        }
    }

    fun searchContact(){

    }

    fun addContact(){

    }

    fun updateContact(){

    }

    fun removeContact(){

    }

}