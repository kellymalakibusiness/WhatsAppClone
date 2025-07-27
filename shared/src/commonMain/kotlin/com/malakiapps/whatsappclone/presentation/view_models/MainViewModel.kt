package com.malakiapps.whatsappclone.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.domain.common.AfterLogOut
import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.LoadingEvent
import com.malakiapps.whatsappclone.domain.common.NavigateToLogin
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.ShowNotification
import com.malakiapps.whatsappclone.domain.common.UserNotFound
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.common.loggerTag1
import com.malakiapps.whatsappclone.domain.common.loggerTag2
import com.malakiapps.whatsappclone.domain.managers.AuthenticationContextManager
import com.malakiapps.whatsappclone.domain.managers.ContactsManager
import com.malakiapps.whatsappclone.domain.managers.EventsManager
import com.malakiapps.whatsappclone.domain.managers.MessagesManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.messages.MessageNotification
import com.malakiapps.whatsappclone.domain.use_cases.InitialAuthenticationCheckUseCase
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.StateValue
import com.malakiapps.whatsappclone.domain.user.UserDetails
import com.malakiapps.whatsappclone.domain.user.UserState
import com.malakiapps.whatsappclone.domain.user.getOrNull
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class MainViewModel(
    private val eventsManager: EventsManager,
    private val userManager: UserManager,
    private val contactsManager: ContactsManager,
    private val initialAuthenticationCheckUseCase: InitialAuthenticationCheckUseCase,
    private val messagesManager: MessagesManager,
    authenticationContextManager: AuthenticationContextManager,
): ViewModel() {

    private val _authenticationContextState: StateFlow<UserState<AuthenticationContext?>> = authenticationContextManager.authenticationContextState

    init {
        viewModelScope.launch {
            userManager
                .userDetailsState
                .filter { it is StateValue }
                .flatMapLatest { userDetailsState ->
                    messagesManager.listenForNotifications().mapNotNull { conversationBrief ->
                        userDetailsState.getOrNull()?.let {
                            it to conversationBrief
                        }
                    }
                }.collect { input ->
                    val userDetails = input.first
                    val conversationBrief = input.second

                    loggerTag2.i { "Checking for contact from notification listener. with context $userDetails" }
                    val contact = contactsManager.getFriendsContacts(listOf(conversationBrief.target)).getOrNull()?.firstOrNull() ?: run {
                        loggerTag2.i { "We on checking for notifications and failed to get contact of ${conversationBrief.target.value}"}
                        eventsManager.sendEvent(OnError(from = this@MainViewModel::class, error = UserNotFound))
                        null
                    }
                    val showNotification = ShowNotification(
                        messageNotification = MessageNotification(
                            targetImage = contact?.image,
                            messageId = conversationBrief.messageId,
                            senderEmail = conversationBrief.sender,
                            name = contact?.name ?: Name(""),
                            message = conversationBrief.value
                        )
                    )
                    eventsManager.sendEvent(showNotification)
                }
        }
    }

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
                        loggerTag1.i { "Logout from lost user profile" }
                        eventsManager.sendEvent(AfterLogOut)
                        eventsManager.sendEvent(NavigateToLogin)
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
            when(useCaseResponse){
                is Response.Failure<AuthenticationContext?, *> -> {
                    //Something went wrong
                    eventsManager.sendEvent(
                        OnError(from = this@MainViewModel::class, error = useCaseResponse.error)
                    )
                    eventsManager.sendEvent(
                        NavigateToLogin
                    )
                }
                is Response.Success<AuthenticationContext?, *> -> Unit
            }
        }
    }

    private fun initiateLogoutOnLostAuthentication(){
        viewModelScope.launch {
            //Add a listener for logout
            _authenticationContextState.collect { onEachValue ->
                if(onEachValue is StateValue && onEachValue.value == null){
                    loggerTag1.i { "Logout from lost user authentication" }
                    eventsManager.sendEvent(AfterLogOut)
                }
            }
        }
    }

    //Helper functions
    suspend fun setLoading(value: Boolean) {
        eventsManager.sendEvent(LoadingEvent(value))
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