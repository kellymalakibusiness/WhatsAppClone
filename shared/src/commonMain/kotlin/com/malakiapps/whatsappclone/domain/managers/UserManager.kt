package com.malakiapps.whatsappclone.domain.managers

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.onEachSuspending
import com.malakiapps.whatsappclone.domain.use_cases.GetUserUseCase
import com.malakiapps.whatsappclone.domain.use_cases.InitializeUserUseCase
import com.malakiapps.whatsappclone.domain.use_cases.OnLoginUpdateAccountUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateUserUseCase
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.ElementUpdateState
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.HasValue
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.None
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserLoading
import com.malakiapps.whatsappclone.domain.user.UserState
import com.malakiapps.whatsappclone.domain.user.UserUpdate
import com.malakiapps.whatsappclone.domain.user.UserValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class UserManager(
    authenticationContextManager: AuthenticationContextManager,
    val getUserUseCase: GetUserUseCase,
    val initializeUserUseCase: InitializeUserUseCase,
    val onLoginUpdateAccountUseCase: OnLoginUpdateAccountUseCase,
    val updateUserUseCase: UpdateUserUseCase,
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _authenticationContextState = authenticationContextManager.authenticationContextState

    private val _userState: MutableStateFlow<UserState> = MutableStateFlow(UserLoading)
    val userState: StateFlow<UserState> = _userState

    init {
        scope.launch {
            _authenticationContextState.collect { onEachValue ->
                if(onEachValue is HasValue){
                    //Check if its user logged in
                    if(onEachValue.value != null){
                        //New user logged in, check if it's the same one or different one
                        if((_userState.value !is UserValue) || onEachValue.value.email != (_userState.value as? UserValue)?.value?.email){
                            initializeUserItem(onEachValue.value)
                        }
                    } else {
                        //User logged out
                        _userState.update { UserValue(null) }
                    }
                }
            }
        }
    }

    private suspend fun initializeUserItem(authenticationContext: AuthenticationContext) {
        val createUserResponse = initializeUserUseCase(authenticationContext)
        createUserResponse.onEachSuspending(
            success = { user ->
                _userState.update {
                    UserValue(user)
                }
            }
        )
    }

    suspend fun initialUpdateUserProfile(email: Email?, name: Name, image: Image?): Response<User, Error> {
        val useCaseResponse = onLoginUpdateAccountUseCase(
            currentUser = getUserOrThrow(),
            email = email,
            name = name,
            image = image
        )

        //React to the result from use case
        useCaseResponse.onEachSuspending(
            success = { user ->
                //Update our userState with the new one
                _userState.update { UserValue(user) }
            }
        )

        return useCaseResponse
    }

    suspend fun updateUser(nameUpdate: ElementUpdateState<Name> = None, aboutUpdate: ElementUpdateState<String> = None, imageUpdate: ElementUpdateState<Image?> = None, addContactUpdate: ElementUpdateState<Email> = None, removeContactUpdate: ElementUpdateState<Email> = None): Response<User, Error> {
        val authenticationContext = getAuthenticationContextOrThrow()
        val userUpdate = UserUpdate(
            email = authenticationContext.email ?: ANONYMOUS_EMAIL,
            name = nameUpdate,
            about = aboutUpdate,
            image = imageUpdate,
            addContact = addContactUpdate,
            removeContact = removeContactUpdate
        )
        val useCaseResponse = updateUserUseCase(
            authenticationContext = authenticationContext,
            userUpdate = userUpdate
        )

        useCaseResponse.onEachSuspending(
            success = { user ->
                _userState.update { UserValue(user) }
            },
        )

        return useCaseResponse
    }

    private fun getUserOrThrow(): User {
        return (userState.value as? UserValue)?.value ?: throw CancellationException("User not found")
    }

    private fun getAuthenticationContextOrThrow(): AuthenticationContext {
        return (_authenticationContextState.value as? HasValue)?.value ?: throw CancellationException("User not authenticated")
    }
}