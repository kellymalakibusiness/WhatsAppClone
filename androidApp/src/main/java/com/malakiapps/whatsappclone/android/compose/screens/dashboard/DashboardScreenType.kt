package com.malakiapps.whatsappclone.android.compose.screens.dashboard

enum class DashboardScreenType(val index: Int) {
    CHATS(0),
    UPDATES(1),
    COMMUNITIES(2),
    CALLS(3);

    companion object {
        fun Int.indexToDashboardScreenType(): DashboardScreenType{
            return when(this){
                0 -> CHATS
                1 -> UPDATES
                2 -> COMMUNITIES
                3 -> CALLS
                else -> throw Exception("Unsupported index")
            }
        }
    }
}