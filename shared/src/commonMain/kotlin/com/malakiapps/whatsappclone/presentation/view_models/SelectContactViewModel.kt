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
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.ProfileType
import com.malakiapps.whatsappclone.domain.user.SearchProfileResult
import com.malakiapps.whatsappclone.domain.user.Some
import com.malakiapps.whatsappclone.domain.user.UserDetails
import com.malakiapps.whatsappclone.domain.user.UserState
import com.malakiapps.whatsappclone.domain.user.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectContactViewModel(
    val userManager: UserManager,
    val contactsManager: ContactsManager
) : ViewModel() {
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

    private val _searchResults = MutableStateFlow<List<SearchProfileResult>?>(null)
    val searchResults: StateFlow<List<SearchProfileResult>?> = _searchResults

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

    private val _helpMessage = MutableStateFlow("")
    val helpMessage: StateFlow<String> = _helpMessage

    init {
        viewModelScope.launch {
            userManager.userDetailsState.collect {
                it.getOrNull()?.let { userDetails ->
                    val userType = userDetails.type
                    _helpMessage.update {
                        when (userType) {
                            UserType.REAL -> "To add new contacts, search for their email to find them."
                            UserType.ANONYMOUS -> "Sign in with a google account to be able to find contacts and send messages"
                        }
                    }
                }
            }
        }
    }

    private suspend fun getUserContacts(userDetails: UserState<UserDetails?>): List<Profile>? {
        _isLoading.update { true }
        val response = userDetails.getOrNull()?.contacts?.let { friendsEmails ->
            if (friendsEmails.isNotEmpty()) {
                val contactsResults = contactsManager.getFriendsContacts(friendsEmails)
                when (contactsResults) {
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
        _isLoading.update { false }
        return response
    }

    fun searchForContact(emailValue: String) {
        if (emailValue.isBlank()) {
            _searchResults.update { null }
            return
        } else {
            viewModelScope.launch {
                _isLoading.update { true }
                //First check if we have it  on our list
                val contactsSearchResults = buildList {
                    //Check for self contact
                    selfProfile.value?.let { currentUser ->
                        if (currentUser.email.value.contains(emailValue) || currentUser.name.value.contains(emailValue)){
                            add(
                                SearchProfileResult(
                                    profile = currentUser,
                                    profileType = ProfileType.OWNER
                                )
                            )
                        }
                    }

                    contacts.value?.forEach {
                        if (it.name.value.contains(emailValue) || it.email.value.contains(emailValue)) {
                            add(
                                SearchProfileResult(
                                    profile = it,
                                    profileType = it.email.getProfileType()
                                )
                            )
                        }
                    }
                }

                if (contactsSearchResults.isEmpty()) {
                    //Try searching for a new contact
                    //Get the string before the @ symbol
                    emailValue.split('@').firstOrNull()?.let {
                        val completeEmail = Email("$it@gmail.com")
                        val response =
                            contactsManager.getFriendsContacts(emails = listOf(completeEmail))
                                .getOrNull()
                                ?.map { profile ->
                                    SearchProfileResult(
                                        profile = profile,
                                        profileType = profile.email.getProfileType()
                                    )
                                } ?: emptyList()
                        _searchResults.update { response }
                    }
                } else {
                    //Return the filtered list
                    _searchResults.update { contactsSearchResults }
                }
                _isLoading.update { false }
            }
        }
    }

    fun addNewContact(email: Email) {
        viewModelScope.launch {
            _isLoading.update { true }
            userManager.updateUserDetails(
                addContactUpdate = Some(email)
            )
            _isLoading.update { false }
        }
    }

    private fun Email.getProfileType(): ProfileType {
        val userDetails =
            userManager.userDetailsState.value.getOrNull() ?: return ProfileType.UNKNOWN

        return if (userDetails.contacts.contains(this)) {
            ProfileType.CONTACT
        } else {
            if (selfProfile.value?.email == this) {
                ProfileType.OWNER
            } else {
                ProfileType.NEW
            }
        }
    }
}