package com.malakiapps.whatsappclone.android.presentation.compose.screens

import kotlinx.serialization.Serializable

sealed interface ScreenDestination


@Serializable
data object DashboardScreenContext: ScreenDestination

@Serializable
object WelcomeLoginScreenContext: ScreenDestination

@Serializable
data class ProfileInfoScreenContext(
    val email: String?,
    val name: String,
    val image: String?,
): ScreenDestination

@Serializable
object ConversationScreenContext: ScreenDestination

@Serializable
object SettingsScreenContext: ScreenDestination

@Serializable
object AccountSettingsScreenContext: ScreenDestination

@Serializable
object ProfileSettingsScreenContext: ScreenDestination

@Serializable
object SettingsProfileUpdateNameScreenContext: ScreenDestination

@Serializable
object SettingsProfileUpdateAboutScreenContext: ScreenDestination

@Serializable
object NewChatScreenContext: ScreenDestination