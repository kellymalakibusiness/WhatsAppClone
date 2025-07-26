package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.UpdatingEvent
import com.malakiapps.whatsappclone.domain.common.onEachSuspending
import com.malakiapps.whatsappclone.domain.managers.EventsManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Some
import kotlinx.coroutines.launch

class UpdateUserProfileViewModel(
    private val eventsManager: EventsManager,
    private val userManager: UserManager
): ViewModel() {

    fun updateUserImage(image: Image?) {
        viewModelScope.launch {
            eventsManager.sendEvent(UpdatingEvent(true))

            val response = userManager.updateUserContact(
                imageUpdate = Some(image)
            )

            response.onEachSuspending(
                failure = { error ->
                    eventsManager.sendEvent(
                        OnError(from = this@UpdateUserProfileViewModel::class, error = error)
                    )
                }
            )
            eventsManager.sendEvent(UpdatingEvent(false))
        }
    }

    fun updateUserName(name: Name) {
        viewModelScope.launch {
            eventsManager.sendEvent(UpdatingEvent(true))

            val response = userManager.updateUserContact(
                nameUpdate = Some(Name(name.value.trim()))
            )

            response.onEachSuspending(
                failure = { error ->
                    eventsManager.sendEvent(
                        OnError(from = this@UpdateUserProfileViewModel::class, error = error)
                    )
                }
            )
            eventsManager.sendEvent(UpdatingEvent(false))
        }
    }

    fun updateUserAbout(about: About) {
        viewModelScope.launch {
            eventsManager.sendEvent(UpdatingEvent(true))

            val response = userManager.updateUserContact(
                aboutUpdate = Some(About(about.value.trim()))
            )

            response.onEachSuspending(
                failure = { error ->
                    eventsManager.sendEvent(
                        OnError(from = this@UpdateUserProfileViewModel::class, error = error)
                    )
                }
            )
            eventsManager.sendEvent(UpdatingEvent(false))
        }
    }
}