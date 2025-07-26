package com.malakiapps.whatsappclone.domain.common

import com.malakiapps.whatsappclone.domain.messages.MessageNotification
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Profile
import kotlin.reflect.KClass

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
    val authenticationContext: AuthenticationContext,
    val initialImage: Image?
): AuthenticationEvent, NavigationEvent

data class NavigateToDashboard(
    val profile: Profile
): AuthenticationEvent, NavigationEvent

data object NavigateToLogin: AuthenticationEvent, NavigationEvent

data class OnError(
    val from: KClass<*>,
    val error: Error
): AuthenticationEvent

data object AfterLogOut: AuthenticationEvent, NavigationEvent

data object PlayMessageTone: Event

data object GoBackToDashboard: Event

data class ShowNotification(
    val messageNotification: MessageNotification
): Event