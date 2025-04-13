package com.malakiapps.whatsappclone.android.screens.communities_screen

data class CommunityItem(
    val name: String,
    val image: Int,
    val groups: List<CommunityGroup>
)

data class CommunityGroup(
    val name: String,
    val image: Int,
    val lastMessage: String
)