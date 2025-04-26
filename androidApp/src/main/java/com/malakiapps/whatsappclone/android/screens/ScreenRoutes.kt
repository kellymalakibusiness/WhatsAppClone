package com.malakiapps.whatsappclone.android.screens


import kotlinx.serialization.Serializable

@Serializable
data object DashboardScreenContext

@Serializable
object WelcomeLoginScreenContext

@Serializable
data class ProfileInfoScreenContext(
    val id: String,
    val name: String,
    val email: String,
)

@Serializable
data object ConversationScreenContext

@Serializable
data object SettingsScreenContext

@Serializable
data object AccountSettingsScreenContext