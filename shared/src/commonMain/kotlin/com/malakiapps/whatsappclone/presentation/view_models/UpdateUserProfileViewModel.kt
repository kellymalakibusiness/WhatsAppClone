package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.Event
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.UpdatingEvent
import com.malakiapps.whatsappclone.domain.common.onEachSuspending
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Some
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class UpdateUserProfileViewModel(
    private val userManager: UserManager
): ViewModel() {
    private val _eventChannel = Channel<Event>()
    val eventsChannelFlow = _eventChannel.receiveAsFlow()

    fun updateUserImage(image: Image?) {
        viewModelScope.launch {
            _eventChannel.send(UpdatingEvent(true))

            val response = userManager.updateUserContact(
                imageUpdate = Some(image)
            )

            response.onEachSuspending(
                failure = { error ->
                    _eventChannel.send(
                        OnError(error)
                    )
                }
            )
            _eventChannel.send(UpdatingEvent(false))
        }
    }

    fun updateUserName(name: Name) {
        viewModelScope.launch {
            _eventChannel.send(UpdatingEvent(true))

            val response = userManager.updateUserContact(
                nameUpdate = Some(name)
            )

            response.onEachSuspending(
                failure = { error ->
                    _eventChannel.send(
                        OnError(error)
                    )
                }
            )
            _eventChannel.send(UpdatingEvent(false))
        }
    }

    fun updateUserAbout(about: About) {
        viewModelScope.launch {
            _eventChannel.send(UpdatingEvent(true))

            val response = userManager.updateUserContact(
                aboutUpdate = Some(about)
            )

            response.onEachSuspending(
                failure = { error ->
                    _eventChannel.send(
                        OnError(error)
                    )
                }
            )
            _eventChannel.send(UpdatingEvent(false))
        }
    }
}