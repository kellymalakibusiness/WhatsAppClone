package com.malakiapps.whatsappclone.domain.common

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

data class UpdatingEvent(
    val isUpdating: Boolean
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

data object LogOut: AuthenticationEvent