package com.malakiapps.whatsappclone.domain.user

data class UserUpdate (
    val email: String,
    val name: Pair<String, Boolean> = Pair("", false),
    val about: Pair<String, Boolean> = Pair("", false),
    val image: Pair<String?, Boolean> = Pair("", false),
    val addContact: Pair<String, Boolean> = Pair("", false),
    val removeContact: Pair<String, Boolean> = Pair("", false)
    )