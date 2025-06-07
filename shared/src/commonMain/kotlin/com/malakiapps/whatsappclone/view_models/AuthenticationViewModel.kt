package com.malakiapps.whatsappclone.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.common.Error
import com.malakiapps.whatsappclone.common.Event
import com.malakiapps.whatsappclone.common.LoadingEvent
import com.malakiapps.whatsappclone.common.NavigateToDashboard
import com.malakiapps.whatsappclone.common.NavigateToLogin
import com.malakiapps.whatsappclone.common.NavigateToProfileInfo
import com.malakiapps.whatsappclone.common.OnError
import com.malakiapps.whatsappclone.common.Response
import com.malakiapps.whatsappclone.common.UpdateUserError
import com.malakiapps.whatsappclone.common.onEach
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.AuthenticationUserState
import com.malakiapps.whatsappclone.domain.user.Initialized
import com.malakiapps.whatsappclone.domain.user.NotInitialized
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserAuthenticationRepository
import com.malakiapps.whatsappclone.domain.user.UserStorageRepository
import com.malakiapps.whatsappclone.domain.user.UserUpdate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class AuthenticationViewModel(
    val authenticationRepository: UserAuthenticationRepository,
    val userStorageRepository: UserStorageRepository,
    val anonymousUserStorageRepository: UserStorageRepository,//UserLocalStorageRepository
) : ViewModel() {
    private val _eventChannel = Channel<Event>()
    val eventsChannelFlow = _eventChannel.receiveAsFlow()

    val userAuthenticationState: StateFlow<AuthenticationUserState> = authenticationRepository
        .getCurrentUser()
        .map {
            Initialized(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotInitialized
        )

    private val _userState: MutableStateFlow<User?> = MutableStateFlow(null)
    val userState: StateFlow<User?> = _userState


    //Dashboard
    private val _dashboardShimmerState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val dashboardShimmerState: StateFlow<Boolean> = _dashboardShimmerState

    init {
        //First lets check if the user is authenticated
        viewModelScope.launch {
            _dashboardShimmerState.value = true
            //Get our first emitted value
            val user = userAuthenticationState.filter { it is Initialized }.first() as Initialized
            if (user.value != null) {
                //Authenticated
                //First check if the user has an account email
                user.value.email?.let { existingEmail ->
                    //Email user
                    //Try reading the user object
                    println("Kelly Email is ${existingEmail} and user is ${user.value.type}")
                    val user =
                        userStorageRepository.getUser(email = existingEmail).getOrElse { error ->
                            _eventChannel.send(
                                OnError(error.error)
                            )
                            _eventChannel.send(
                                NavigateToLogin
                            )
                        }.data

                    //Update our user state
                    _userState.update {
                        user
                    }
                } ?: run {
                    //Anonymous account
                    //Read anonymous user item
                    val user = anonymousUserStorageRepository.getUser(email = "anonymous").getOrElse { error ->
                        _eventChannel.send(
                            OnError(error.error)
                        )
                        _eventChannel.send(
                            NavigateToLogin
                        )
                    }.data
                    //Update our user state
                    _userState.update {
                        user
                    }
                }
            } else {
                _eventChannel.send(
                    NavigateToLogin
                )
            }
            _eventChannel.send(LoadingEvent(false))
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _eventChannel.send(LoadingEvent(true))
            val authenticationUser = authenticationRepository.signIn().getOrElse { error ->
                _eventChannel.send(
                    OnError(error.error)
                )
            }.data

            createUser(authenticationUser)

            _eventChannel.send(
                NavigateToProfileInfo(
                    authenticationUser = authenticationUser
                )
            )
        }
    }

    fun initialUpdateUserProfile(email: String?, name: String? = null, image: String? = null) {
        viewModelScope.launch {
            _eventChannel.send(LoadingEvent(true))
            //No need to update the authentication name
            val nameUpdate = name?.let {
                Pair(it, true)
            } ?: Pair("", false)

            val imageUpdate = image?.let {
                Pair(it, true)
            } ?: Pair("", false)

            //Check if the email exists
            val user = email?.let { existingEmail ->
                userStorageRepository.updateUser(
                    userUpdate = UserUpdate(
                        email = existingEmail,
                        name = nameUpdate,
                        image = imageUpdate
                    )
                ).getOrElse { error ->
                    _eventChannel.send(
                        OnError(error.error)
                    )
                }.data
            } ?: run {
                anonymousUserStorageRepository.updateUser(
                    userUpdate = UserUpdate(
                        email = "anonymous",
                        name = nameUpdate,
                        image = imageUpdate
                    )
                ).getOrElse { error ->
                    _eventChannel.send(
                        OnError(error.error)
                    )
                }.data
            }.also {
                //Update our userState with the new one
                _userState.update { it }
            }

            _eventChannel.send(
                NavigateToDashboard(
                    user = user
                )
            )
        }
    }

    fun anonymousSignIn() {
        viewModelScope.launch {
            _eventChannel.send(LoadingEvent(true))
            val authenticationUser = authenticationRepository.anonymousSignIn().getOrElse { error ->
                _eventChannel.send(
                    OnError(error.error)
                )
            }.data

            createUser(authenticationUser)

            _eventChannel.send(
                NavigateToProfileInfo(
                    authenticationUser = authenticationUser
                )
            )
        }
    }

    private suspend fun createUser(authenticationUser: AuthenticationUser): User? {
        return (authenticationUser.email?.let { availableEmail ->
            userStorageRepository.createUser(availableEmail, authenticationUser)
                .getOrElse { error ->
                    _eventChannel.send(
                        OnError(error.error)
                    )
                }.data
        } ?: run {
            anonymousUserStorageRepository.createUser("anonymous", authenticationUser)
                .getOrElse { error ->
                    _eventChannel.send(
                        OnError(error.error)
                    )
                }.data
        }).also {
            //Update our userState with the new one
            _userState.update {
                it
            }
        }
    }

    private suspend fun updateUser(userUpdate: UserUpdate): Response<User, UpdateUserError> {
        return userStorageRepository.updateUser(userUpdate).also {
            it.onEach(
                success = {
                    //Update our userState
                    _userState.update {
                        it
                    }
                }
            )
        }
    }

    fun logOut() {
        viewModelScope.launch {
            _eventChannel.send(LoadingEvent(true))
            authenticationRepository.signOut()
            _eventChannel.send(NavigateToLogin)
        }
    }

    suspend fun setLoading(value: Boolean){
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