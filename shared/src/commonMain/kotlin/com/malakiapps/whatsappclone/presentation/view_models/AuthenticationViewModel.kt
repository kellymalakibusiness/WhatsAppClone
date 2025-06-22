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
import com.malakiapps.whatsappclone.domain.managers.AuthenticationContextManager
import com.malakiapps.whatsappclone.domain.use_cases.InitialAuthenticationCheckUseCase
import com.malakiapps.whatsappclone.domain.use_cases.LogoutUseCase
import com.malakiapps.whatsappclone.domain.use_cases.SignInUseCase
import com.malakiapps.whatsappclone.domain.user.AuthenticationContextState
import com.malakiapps.whatsappclone.domain.user.HasValue
import com.malakiapps.whatsappclone.domain.user.SignInResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    val signInUseCase: SignInUseCase,
    val logoutUseCase: LogoutUseCase,
    val initialAuthenticationCheckUseCase: InitialAuthenticationCheckUseCase,
    authenticationContextManager: AuthenticationContextManager,
): ViewModel() {
    private val _eventChannel = Channel<Event>()
    val eventsChannelFlow = _eventChannel.receiveAsFlow()

    private val _authenticationContextState: StateFlow<AuthenticationContextState> = authenticationContextManager.authenticationContextState

    init {
        //First lets check if the user is authenticated
        viewModelScope.launch {
            val useCaseResponse = initialAuthenticationCheckUseCase(
                userAuthenticationState = _authenticationContextState
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

            //Add a listener for logout
            _authenticationContextState.collect { onEachValue ->
                if(onEachValue is HasValue && onEachValue.value == null){
                    logOut()
                }
            }
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

    private suspend fun signIn(repositoryCall: suspend () -> Response<SignInResponse, Error>) {
        _eventChannel.send(LoadingEvent(true))

        val useCaseResponse = repositoryCall()

        _eventChannel.send(LoadingEvent(false))
        //React to the result from use case
        useCaseResponse.onEachSuspending(
            success = {
                _eventChannel.send(
                    NavigateToProfileInfo(
                        authenticationContext = it.authenticationContext,
                        initialImage = it.initialBase64ProfileImage
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