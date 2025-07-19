package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Event
import com.malakiapps.whatsappclone.domain.common.LoadingEvent
import com.malakiapps.whatsappclone.domain.common.LogOut
import com.malakiapps.whatsappclone.domain.common.NavigateToLogin
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UserNotFound
import com.malakiapps.whatsappclone.domain.common.onEachSuspending
import com.malakiapps.whatsappclone.domain.managers.AuthenticationContextManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.use_cases.InitialAuthenticationCheckUseCase
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.StateValue
import com.malakiapps.whatsappclone.domain.user.UserDetails
import com.malakiapps.whatsappclone.domain.user.UserState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class MainViewModel(
    private val userManager: UserManager,
    private val initialAuthenticationCheckUseCase: InitialAuthenticationCheckUseCase,
    authenticationContextManager: AuthenticationContextManager,
): ViewModel() {

    private val _eventChannel = Channel<Event>()
    val eventsChannelFlow = _eventChannel.receiveAsFlow()

    private val _authenticationContextState: StateFlow<UserState<AuthenticationContext?>> = authenticationContextManager.authenticationContextState

    val selfProfileState: StateFlow<Profile?> = userManager.userProfileState.map { userState ->
        if(userState is StateValue<Profile?>){
            userState.value
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val userDetails: StateFlow<UserDetails?> = userManager.userDetailsState.map { userDetailsState ->
        if(userDetailsState is StateValue<UserDetails?>){
            userDetailsState.value
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        initiateInitialCheckForUserAuthentication()
        initiateLogoutOnLostAuthentication()
        initiateLogoutOnUserNotFoundCoroutine()
    }

    private fun initiateLogoutOnUserNotFoundCoroutine(){
        viewModelScope.launch {
            //Check for when we don't have the user item
            userManager.userProfileState.collect { currentState ->
                if (currentState is StateValue<Profile?> && currentState.value == null) {
                    //Check if the user initiated logging out by checking authentication state
                    val currentAuthState = _authenticationContextState.value
                    if(currentAuthState is StateValue<AuthenticationContext?> && currentAuthState.value != null){
                        _eventChannel.send(LogOut)
                        _eventChannel.send(NavigateToLogin)
                        _eventChannel.send(
                            OnError(UserNotFound)
                        )
                    }
                }
            }
        }
    }

    private fun initiateInitialCheckForUserAuthentication(){
        viewModelScope.launch {
            val useCaseResponse = initialAuthenticationCheckUseCase(
                userAuthenticationState = _authenticationContextState
            )

            //React to the result from use case
            useCaseResponse.onEachSuspending(
                failure = { error ->
                    //Something went wrong
                    _eventChannel.send(
                        OnError(error)
                    )
                    _eventChannel.send(
                        NavigateToLogin
                    )
                }
            )
        }
    }

    private fun initiateLogoutOnLostAuthentication(){
        viewModelScope.launch {
            //Add a listener for logout
            _authenticationContextState.collect { onEachValue ->
                if(onEachValue is StateValue && onEachValue.value == null){
                    _eventChannel.send(LogOut)
                }
            }
        }
    }

    //Helper functions
    suspend fun setLoading(value: Boolean) {
        _eventChannel.send(LoadingEvent(value))
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