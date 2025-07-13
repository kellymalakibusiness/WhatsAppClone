package com.malakiapps.whatsappclone.domain.common

const val USERS_COLLECTION_NAME = "users"
const val USERS_DETAILS_COLLECTION_NAME = "details"
const val USERS_PROFILE_DOCUMENT_NAME = "profile"

const val MESSAGES_COLLECTION_NAME = "messages"

const val CONVERSATION_COLLECTION_NAME = "conversation"

data object UserAttributeKeys {
    const val NAME = "name"
    const val EMAIL = "email"
    const val ABOUT = "about"
    const val CONTACTS = "contacts"
    const val IMAGE = "image"
}

data object MessageAttributeKeys {
    const val MESSAGE_ID = "messageId"

    const val SENDER = "sender"

    const val RECEIVER = "receiver"

    const val TIME = "time"

    const val VALUE = "value"

    const val MESSAGE_ATTRIBUTES = "attributes"

    const val VIEW_STATUS = "view_status"

    const val IS_UPDATED = "is_updated"

    const val IS_DELETED = "is_deleted"

    const val SENDER_REACTION = "sender_reaction"

    const val RECEIVER_REACTION = "receiver_reaction"
}