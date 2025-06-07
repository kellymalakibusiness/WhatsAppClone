package com.malakiapps.whatsappclone.common

import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.User

//Categories
sealed interface Event

sealed interface AuthenticationEvent: Event
sealed interface NavigationEvent: Event

//The events
data class LoadingEvent(
    val isLoading: Boolean
): Event

data class NavigateToProfileInfo(
    val authenticationUser: AuthenticationUser
): AuthenticationEvent, NavigationEvent

data class NavigateToDashboard(
    val user: User
): AuthenticationEvent, NavigationEvent

data object NavigateToLogin: AuthenticationEvent, NavigationEvent

data class OnError(
    val error: Error
): AuthenticationEvent