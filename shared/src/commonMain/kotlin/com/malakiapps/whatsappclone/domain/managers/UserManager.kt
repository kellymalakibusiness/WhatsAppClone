package com.malakiapps.whatsappclone.domain.managers

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.InvalidUpdate
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.use_cases.GetUserContactUseCase
import com.malakiapps.whatsappclone.domain.use_cases.InitializeUserUseCase
import com.malakiapps.whatsappclone.domain.use_cases.MigrateToGoogleAccountUseCase
import com.malakiapps.whatsappclone.domain.use_cases.OnLoginUpdateAccountUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateUserContactUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateUserDetailsUseCase
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.ElementUpdateState
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.None
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.SignInResponse
import com.malakiapps.whatsappclone.domain.user.StateLoading
import com.malakiapps.whatsappclone.domain.user.StateValue
import com.malakiapps.whatsappclone.domain.user.UserContactUpdate
import com.malakiapps.whatsappclone.domain.user.UserDetails
import com.malakiapps.whatsappclone.domain.user.UserDetailsUpdate
import com.malakiapps.whatsappclone.domain.user.UserState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class UserManager(
    private val authenticationContextManager: AuthenticationContextManager,
    private val getUserContactUseCase: GetUserContactUseCase,
    private val initializeUserUseCase: InitializeUserUseCase,
    private val onLoginUpdateAccountUseCase: OnLoginUpdateAccountUseCase,
    private val updateUserContactUseCase: UpdateUserContactUseCase,
    private val updateUserDetailsUseCase: UpdateUserDetailsUseCase,
    private val migrateToGoogleAccountUseCase: MigrateToGoogleAccountUseCase
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _selfProfileState: MutableStateFlow<UserState<Profile?>> = MutableStateFlow(StateLoading)
    val userProfileState: StateFlow<UserState<Profile?>> = _selfProfileState

    private val _userDetailsState: MutableStateFlow<UserState<UserDetails?>> = MutableStateFlow(StateLoading)
    val userDetailsState: StateFlow<UserState<UserDetails?>> = _userDetailsState

    init {
        scope.launch {
            authenticationContextManager.authenticationContextState.collect { onEachValue ->
                if(onEachValue is StateValue<AuthenticationContext?>){
                    //Check if its user logged in
                    if(onEachValue.value != null){
                        //New user logged in
                        initializeUserItem(onEachValue.value)
                    } else {
                        //User logged out
                        _selfProfileState.update { StateValue(null) }
                    }
                }
            }
        }
    }
    private suspend fun initializeUserItem(authenticationContext: AuthenticationContext) {
        val createUserResponse = initializeUserUseCase(authenticationContext)
        when(createUserResponse){
            is Response.Failure<Pair<Profile, UserDetails>, Error> -> Unit
            is Response.Success<Pair<Profile, UserDetails>, Error> -> {
                _selfProfileState.update {
                    StateValue(createUserResponse.data.first)
                }
                _userDetailsState.update {
                    StateValue(createUserResponse.data.second)
                }
            }
        }
    }

    suspend fun initialUpdateUserProfile(email: Email?, name: Name, image: Image?): Response<Profile, Error> {
        val useCaseResponse = onLoginUpdateAccountUseCase(
            currentProfile = getCurrentUserContactOrThrow(),
            email = email,
            name = Name(name.value.trim()),
            image = image
        )

        //React to the result from use case
        useCaseResponse.getOrNull()?.let { response ->
            _selfProfileState.update { StateValue(response) }
        }

        return useCaseResponse
    }

    suspend fun updateUserFromAnonymousAccount(signInResponse: SignInResponse) {
        val response = migrateToGoogleAccountUseCase.invoke(signInResponse)

        response.getOrNull()?.let { profile ->
            _selfProfileState.update { StateValue(profile) }
        }

        authenticationContextManager.updateAuthentication(signInResponse.authenticationContext)
    }

    suspend fun updateUserContact(nameUpdate: ElementUpdateState<Name> = None, aboutUpdate: ElementUpdateState<About> = None, imageUpdate: ElementUpdateState<Image?> = None): Response<Profile, Error> {
        val authenticationContext = getAuthenticationContextOrThrow()
        val userContactUpdate = UserContactUpdate(
            email = authenticationContext.email ?: ANONYMOUS_EMAIL,
            name = nameUpdate,
            about = aboutUpdate,
            image = imageUpdate,
        )
        val useCaseResponse = updateUserContactUseCase(
            authenticationContext = authenticationContext,
            userContactUpdate = userContactUpdate
        )

        useCaseResponse.getOrNull()?.let { response ->
            _selfProfileState.update { StateValue(response) }
        }

        return useCaseResponse
    }


    suspend fun updateUserDetails(addContactUpdate: ElementUpdateState<Email> = None, removeContactUpdate: ElementUpdateState<Email> = None): Response<UserDetails, Error> {
        val authenticationContext = getAuthenticationContextOrThrow()

        return authenticationContext.email?.let { availableEmail ->
            val userDetailsUpdate = UserDetailsUpdate(
                email = availableEmail,
                addContact = addContactUpdate,
                removeContact = removeContactUpdate
            )
            val useCaseResponse = updateUserDetailsUseCase(
                userDetailsUpdate = userDetailsUpdate
            )

            useCaseResponse.getOrNull()?.let { userDetails ->
                _userDetailsState.update { StateValue(userDetails) }
            }

            useCaseResponse
        } ?: Response.Failure(InvalidUpdate("Update not supported for anonymous users"))
    }

    private fun getCurrentUserContactOrThrow(): Profile {
        return (userProfileState.value as? StateValue<Profile?>)?.value ?: throw CancellationException("User not found")
    }

    private fun getAuthenticationContextOrThrow(): AuthenticationContext {
        return (authenticationContextManager.authenticationContextState.value as? StateValue)?.value ?: throw CancellationException("User not authenticated")
    }
}