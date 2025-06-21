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
import com.malakiapps.whatsappclone.domain.common.onEachSuspending
import com.malakiapps.whatsappclone.domain.use_cases.InitializeUserUseCase
import com.malakiapps.whatsappclone.domain.use_cases.GetUserUseCase
import com.malakiapps.whatsappclone.domain.use_cases.OnLoginUpdateAccountUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateUserUseCase
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Update
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserUpdate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class UserViewModel(
    val getUserUseCase: GetUserUseCase,
    val initializeUserUseCase: InitializeUserUseCase,
    val onLoginUpdateAccountUseCase: OnLoginUpdateAccountUseCase,
    val updateUserUseCase: UpdateUserUseCase,
): ViewModel() {

    private val _eventChannel = Channel<Event>()
    val eventsChannelFlow = _eventChannel.receiveAsFlow()

    private val _userState: MutableStateFlow<User?> = MutableStateFlow(null)
    val userState: StateFlow<User?> = _userState

    private var authenticationContextState: AuthenticationContext? = null

    fun initializeUserItem(authenticationContext: AuthenticationContext){
        viewModelScope.launch {
            if (userState.value == null || userState.value?.email != authenticationContext.email){
                val createUserResponse = initializeUserUseCase(authenticationContext)
                createUserResponse.onEachSuspending(
                    success = { user ->
                        authenticationContextState = authenticationContext
                        _userState.update {
                            user
                        }
                    },
                    failure = {
                        //Something went wrong
                        authenticationContextState = null
                        _eventChannel.send(LogOut)
                        _eventChannel.send(NavigateToLogin)
                        _eventChannel.send(
                            OnError(it)
                        )
                    }
                )
            }
        }
    }

    fun initialUpdateUserProfile(email: Email?, name: Name, image: Image?) {
        viewModelScope.launch {
            _eventChannel.send(UpdatingEvent(true))
            val useCaseResponse = onLoginUpdateAccountUseCase(
                currentUser = userState.value,
                email = email,
                name = name,
                image = image
            )

            //React to the result from use case
            useCaseResponse.onEachSuspending(
                success = { user ->
                    //Update our userState with the new one
                    _userState.update { user }

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
            val currentUser = getAuthenticationContext()

            val userUpdate = UserUpdate(
                email = currentUser.email ?: ANONYMOUS_EMAIL,
                image = Update(image)
            )

            val useCaseResponse = updateUserUseCase(
                authenticationContext = currentUser,
                userUpdate = userUpdate
            )

            useCaseResponse.onEachSuspending(
                success = { user ->
                    _userState.update { user }
                },
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
            val currentAuthCtx = getAuthenticationContext()

            val userUpdate = UserUpdate(
                email = currentAuthCtx.email ?: ANONYMOUS_EMAIL,
                name = Update(name)
            )

            val useCaseResponse = updateUserUseCase(
                authenticationContext = currentAuthCtx,
                userUpdate = userUpdate
            )

            useCaseResponse.onEachSuspending(
                success = { user ->
                    _userState.update { user }
                },
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

            val currentAuthCtx = getAuthenticationContext()

            val userUpdate = UserUpdate(
                email = currentAuthCtx.email ?: ANONYMOUS_EMAIL,
                about = Update(about)
            )

            val useCaseResponse = updateUserUseCase(
                authenticationContext = currentAuthCtx,
                userUpdate = userUpdate
            )

            useCaseResponse.onEachSuspending(
                success = { user ->
                    _userState.update { user }
                },
                failure = { error ->
                    _eventChannel.send(
                        OnError(error)
                    )
                }
            )
            _eventChannel.send(UpdatingEvent(false))
        }
    }


    fun getAuthenticationContext(): AuthenticationContext {
        return authenticationContextState ?: throw CancellationException("User not authenticated")
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