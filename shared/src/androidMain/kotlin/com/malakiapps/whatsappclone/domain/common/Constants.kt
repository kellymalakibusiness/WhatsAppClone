package com.malakiapps.whatsappclone.domain.common

const val USERS_COLLECTION_NAME = "users"
const val USERS_DETAILS_COLLECTION_NAME = "details"
const val USERS_PROFILE_DOCUMENT_NAME = "profile"

data object UserAttributeKeys {
    const val ID = "id"
    const val NAME = "name"
    const val EMAIL = "email"
    const val ABOUT = "about"
    const val CONTACTS = "contacts"
    const val IMAGE = "image"
}