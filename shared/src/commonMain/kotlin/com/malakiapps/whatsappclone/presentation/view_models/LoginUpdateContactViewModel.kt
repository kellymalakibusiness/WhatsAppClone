package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.Event
import com.malakiapps.whatsappclone.domain.common.NavigateToDashboard
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.UpdatingEvent
import com.malakiapps.whatsappclone.domain.common.onEachSuspending
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginUpdateContactViewModel(
    val userManager: UserManager,
): ViewModel() {

    private val _eventChannel = Channel<Event>()
    val eventsChannelFlow = _eventChannel.receiveAsFlow()

    fun updateUserProfile(email: Email?, name: Name, image: Image?) {
        viewModelScope.launch {
            _eventChannel.send(UpdatingEvent(true))
            val response = userManager.initialUpdateUserProfile(email = email, name = name, image = image)

            //React to the result
            response.onEachSuspending(
                success = { user ->
                    _eventChannel.send(
                        NavigateToDashboard(
                            profile = user
                        )
                    )
                },
                failure = { error ->
                    _eventChannel.send(
                        OnError(error)
                    )
                }
            )
        }
    }
}