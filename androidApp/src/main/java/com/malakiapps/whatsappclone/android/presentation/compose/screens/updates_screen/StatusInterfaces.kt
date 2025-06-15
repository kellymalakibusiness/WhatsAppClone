package com.malakiapps.whatsappclone.android.presentation.compose.screens.updates_screen

sealed interface StatusCard

data class UserStatusCard(
    val userProfile: Int,
    val statusImage: Int,
    val name: String,
    val isViewed: Boolean
): StatusCard


data class AddStatusCard(
    val userProfile: Int
): StatusCard