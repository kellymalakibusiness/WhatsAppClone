package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.NavigateToDashboard
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UpdatingEvent
import com.malakiapps.whatsappclone.domain.managers.EventsManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Profile
import kotlinx.coroutines.launch

class LoginUpdateContactViewModel(
    private val eventsManager: EventsManager,
    val userManager: UserManager,
): ViewModel() {

    fun updateUserProfile(email: Email?, name: Name, image: Image?) {
        viewModelScope.launch {
            eventsManager.sendEvent(UpdatingEvent(true))
            val response = userManager.initialUpdateUserProfile(email = email, name = name, image = image)

            //React to the result
            when(response){
                is Response.Failure<Profile, *> -> {
                    eventsManager.sendEvent(
                        OnError(from = this@LoginUpdateContactViewModel::class, error = response.error)
                    )
                }
                is Response.Success<Profile, *> -> {
                    eventsManager.sendEvent(
                        NavigateToDashboard(
                            profile = response.data
                        )
                    )
                }
            }
        }
    }
}