package com.malakiapps.whatsappclone.android.presentation.compose.screens

import com.malakiapps.whatsappclone.domain.user.Email
import kotlinx.serialization.Serializable

sealed interface ScreenDestination


@Serializable
data object DashboardScreenRoute: ScreenDestination

@Serializable
object LoginWelcomeScreenRoute: ScreenDestination

@Serializable
data class LoginUpdateProfileScreenRoute(
    val email: String?,
    val name: String,
    val image: String?,
): ScreenDestination

@Serializable
data class ConversationScreenRoute(
    val email: String
): ScreenDestination

@Serializable
object SettingsScreenRoute: ScreenDestination

@Serializable
object AccountSettingsScreenRoute: ScreenDestination

@Serializable
object ProfileSettingsScreenRoute: ScreenDestination

@Serializable
object SettingsProfileUpdateNameScreenRoute: ScreenDestination

@Serializable
object SettingsProfileUpdateAboutScreenRoute: ScreenDestination

@Serializable
object SelectContactScreenRoute: ScreenDestination