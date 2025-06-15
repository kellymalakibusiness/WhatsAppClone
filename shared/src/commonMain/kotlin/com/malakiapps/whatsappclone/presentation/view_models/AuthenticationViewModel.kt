package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Event
import com.malakiapps.whatsappclone.domain.common.LoadingEvent
import com.malakiapps.whatsappclone.domain.common.NavigateToLogin
import com.malakiapps.whatsappclone.domain.common.NavigateToProfileInfo
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.onEachSuspending
import com.malakiapps.whatsappclone.domain.use_cases.GetUserAuthenticationStateUseCase
import com.malakiapps.whatsappclone.domain.use_cases.InitialAuthenticationCheckUseCase
import com.malakiapps.whatsappclone.domain.use_cases.LogoutUseCase
import com.malakiapps.whatsappclone.domain.use_cases.SignInUseCase
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.AuthenticationUserState
import com.malakiapps.whatsappclone.domain.user.NotInitialized
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    val signInUseCase: SignInUseCase,
    val logoutUseCase: LogoutUseCase,
    val initialAuthenticationCheckUseCase: InitialAuthenticationCheckUseCase,
    getUserAuthenticationStateUseCase: GetUserAuthenticationStateUseCase,
): ViewModel() {
    private val _eventChannel = Channel<Event>()
    val eventsChannelFlow = _eventChannel.receiveAsFlow()

    val userAuthenticationState: StateFlow<AuthenticationUserState> = getUserAuthenticationStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotInitialized
        )

    init {
        //First lets check if the user is authenticated
        viewModelScope.launch {
            val useCaseResponse = initialAuthenticationCheckUseCase(
                userAuthenticationState = userAuthenticationState
            )

            //React to the result from use case
            useCaseResponse.onEachSuspending(
                success = { user ->
                    if(user == null){
                        //User has not signed up
                        _eventChannel.send(
                            NavigateToLogin
                        )
                    }
                },
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



    fun signInWithGoogle() {
        viewModelScope.launch {
            signIn(
                repositoryCall = {
                    signInUseCase.signInWithGoogle()
                }
            )
        }
    }

    fun anonymousSignIn() {
        viewModelScope.launch {
            signIn(
                repositoryCall = {
                    signInUseCase.signInAnonymously()
                }
            )
        }
    }

    fun logOut() {
        viewModelScope.launch {
            _eventChannel.send(LoadingEvent(true))
            logoutUseCase()
            _eventChannel.send(NavigateToLogin)
        }
    }

    private suspend fun signIn(repositoryCall: suspend () -> Response<AuthenticationUser, Error>) {
        _eventChannel.send(LoadingEvent(true))

        val useCaseResponse = repositoryCall()

        _eventChannel.send(LoadingEvent(false))
        //React to the result from use case
        useCaseResponse.onEachSuspending(
            success = {
                _eventChannel.send(
                    NavigateToProfileInfo(
                        authenticationUser = it
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

    //Helper functions
    suspend fun setLoading(value: Boolean) {
        _eventChannel.send(LoadingEvent(value))
    }
}