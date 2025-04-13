package com.malakiapps.whatsappclone.android.screens


import kotlinx.serialization.Serializable

@Serializable
sealed interface DashboardScreenContext

@Serializable
object WelcomeLoginScreenContext

@Serializable
data class ProfileInfoScreenContext(
    val id: String,
    val name: String,
    val email: String,
)


@Serializable
data object ChatScreenContext: DashboardScreenContext

@Serializable
data object ConversationScreenContext

@Serializable
data object UpdatesScreenContext: DashboardScreenContext

@Serializable
data object CommunitiesScreenContext: DashboardScreenContext

@Serializable
data object CallScreenContext: DashboardScreenContext

@Serializable
data object SettingsScreenContext: DashboardScreenContext