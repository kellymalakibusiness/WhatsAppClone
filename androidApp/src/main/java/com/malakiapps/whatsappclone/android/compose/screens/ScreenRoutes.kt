package com.malakiapps.whatsappclone.android.compose.screens

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