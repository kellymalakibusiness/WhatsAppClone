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
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
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

    private var authenticationUserState: AuthenticationUser? = null

    fun initializeUserItem(authenticationUser: AuthenticationUser){
        viewModelScope.launch {
            if (userState.value == null || userState.value?.email != authenticationUser.email){
                val createUserResponse = initializeUserUseCase(authenticationUser)
                createUserResponse.onEachSuspending(
                    success = { user ->
                        authenticationUserState = authenticationUser
                        _userState.update {
                            user
                        }
                    },
                    failure = {
                        //Something went wrong
                        authenticationUserState = null
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

    fun initialUpdateUserProfile(email: String?, name: String? = null, image: String? = null) {
        viewModelScope.launch {
            _eventChannel.send(UpdatingEvent(true))
            val useCaseResponse = onLoginUpdateAccountUseCase(
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

    fun updateUserImage(image: String?) {
        viewModelScope.launch {
            _eventChannel.send(UpdatingEvent(true))
            val currentUser = getUser()

            val userUpdate = UserUpdate(
                email = currentUser.email ?: "anonymous",
                image = Pair(image, true)
            )

            val useCaseResponse = updateUserUseCase(
                authenticationUser = currentUser,
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

    fun updateUserName(name: String) {
        viewModelScope.launch {
            _eventChannel.send(UpdatingEvent(true))
            val currentUser = getUser()

            val userUpdate = UserUpdate(
                email = currentUser.email ?: "anonymous",
                name = Pair(name, true)
            )

            val useCaseResponse = updateUserUseCase(
                authenticationUser = currentUser,
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

            val currentUser = getUser()

            val userUpdate = UserUpdate(
                email = currentUser.email ?: "anonymous",
                about = Pair(about, true)
            )

            val useCaseResponse = updateUserUseCase(
                authenticationUser = currentUser,
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


    fun getUser(): AuthenticationUser {
        return authenticationUserState ?: throw CancellationException("User not authenticated")
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