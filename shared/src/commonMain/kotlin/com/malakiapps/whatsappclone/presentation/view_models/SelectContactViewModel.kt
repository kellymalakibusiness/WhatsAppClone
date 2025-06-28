package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.Event
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.managers.ContactsManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.getOrNull
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.emptyList
import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.user.UserDetails
import com.malakiapps.whatsappclone.domain.user.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SelectContactViewModel(
    val userManager: UserManager,
    val contactsManager: ContactsManager
): ViewModel() {
    private val _eventChannel = Channel<Event>()
    val eventsChannelFlow = _eventChannel.receiveAsFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val contacts: StateFlow<List<Profile>?> = userManager.userDetailsState.map {
        getUserContacts(userDetails = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val selfProfile: StateFlow<Profile?> = userManager.userProfileState.map {
        it.getOrNull()?.let { availableProfile ->
            availableProfile.copy(
                name = Name("${availableProfile.name.value} (You)"),
                about = About("Message yourself")
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private suspend fun getUserContacts(userDetails: UserState<UserDetails?>): List<Profile>? {
        _isLoading.update { true }
        val response = userDetails.getOrNull()?.contacts?.let { friendsEmails ->
            if(friendsEmails.isNotEmpty()){
                val contactsResults = contactsManager.getFriendsContacts(friendsEmails)
                when(contactsResults){
                    is Response.Failure<List<Profile>, Error> -> {
                        _eventChannel.send(
                            OnError(contactsResults.error)
                        )
                        null
                    }
                    is Response.Success<List<Profile>, Error> -> {
                        contactsResults.data
                    }
                }
            } else {
                emptyList()
            }
        }
        _isLoading.update{ false }
        return response
    }

    fun searchForContact(){

    }
}