package com.malakiapps.whatsappclone.domain.managers

import com.malakiapps.whatsappclone.domain.common.AuthenticationError
import com.malakiapps.whatsappclone.domain.common.AuthenticationUserNotFound
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.AuthenticationRepository
import com.malakiapps.whatsappclone.domain.user.StateLoading
import com.malakiapps.whatsappclone.domain.user.StateValue
import com.malakiapps.whatsappclone.domain.user.UserState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthenticationContextManager(
    private val authenticationRepository: AuthenticationRepository,
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _authenticationContextState = MutableStateFlow<UserState<AuthenticationContext?>>(StateLoading)
    val authenticationContextState: StateFlow<UserState<AuthenticationContext?>> = _authenticationContextState

    init {
        scope.launch {
            authenticationRepository
                .getAuthContextState()
                .collect { newValue ->
                    _authenticationContextState.update {
                        StateValue(newValue)
                    }
                }
        }
    }

    private fun getAuthContext(): Response<AuthenticationContext, AuthenticationError> {
        return authenticationRepository.getAuthContext()?.let {
            Response.Success(it)
        } ?: Response.Failure(AuthenticationUserNotFound)
    }
}