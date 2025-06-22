package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Event
import com.malakiapps.whatsappclone.domain.common.LogOut
import com.malakiapps.whatsappclone.domain.common.NavigateToDashboard
import com.malakiapps.whatsappclone.domain.common.NavigateToLogin
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UpdatingEvent
import com.malakiapps.whatsappclone.domain.common.UserNotFound
import com.malakiapps.whatsappclone.domain.common.onEachSuspending
import com.malakiapps.whatsappclone.domain.managers.AuthenticationContextManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.use_cases.InitializeUserUseCase
import com.malakiapps.whatsappclone.domain.use_cases.GetUserUseCase
import com.malakiapps.whatsappclone.domain.use_cases.InitialAuthenticationCheckUseCase
import com.malakiapps.whatsappclone.domain.use_cases.OnLoginUpdateAccountUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateUserUseCase
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.HasValue
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Update
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserState
import com.malakiapps.whatsappclone.domain.user.UserUpdate
import com.malakiapps.whatsappclone.domain.user.UserValue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class UserViewModel(
    val userManager: UserManager,
): ViewModel() {

    private val _eventChannel = Channel<Event>()
    val eventsChannelFlow = _eventChannel.receiveAsFlow()

    private val _userState: StateFlow<UserState> = userManager.userState
    val userState: StateFlow<User?> = _userState.map { userState ->
        if(userState is UserValue){
            userState.value
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            //Check for when we don't have the user item
            _userState.collect { currentState ->
                if (currentState is UserValue && currentState.value == null) {
                    _eventChannel.send(LogOut)
                    _eventChannel.send(NavigateToLogin)
                    _eventChannel.send(
                        OnError(UserNotFound)
                    )
                }
            }
        }
    }

    fun initialUpdateUserProfile(email: Email?, name: Name, image: Image?) {
        viewModelScope.launch {
            _eventChannel.send(UpdatingEvent(true))
            val response = userManager.initialUpdateUserProfile(email = email, name = name, image = image)

            //React to the result
            response.onEachSuspending(
                success = { user ->
                    _eventChannel.send(
                        NavigateToDashboard(
                            user = user
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

    fun updateUserImage(image: Image?) {
        viewModelScope.launch {
            _eventChannel.send(UpdatingEvent(true))

            val response = userManager.updateUser(
                imageUpdate = Update(image)
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

            val response = userManager.updateUser(
                nameUpdate = Update(name)
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

    fun updateUserAbout(about: String) {
        viewModelScope.launch {
            _eventChannel.send(UpdatingEvent(true))

            val response = userManager.updateUser(
                aboutUpdate = Update(about)
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

    private suspend fun <R> Response<R, Error>.getOrElse(onError: suspend (Response.Failure<R, Error>) -> Unit): Response.Success<R, Error> {
        return when (this) {
            is Response.Failure<R, Error> -> {
                onError(this)
                throw CancellationException("An error occurred. No need to continue")
            }

            is Response.Success<R, Error> -> {
                this
            }
        }
    }

}