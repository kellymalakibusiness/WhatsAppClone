package com.malakiapps.whatsappclone.domain.common

const val USERS_COLLECTION_NAME = "users"
const val USERS_DETAILS_COLLECTION_NAME = "details"
const val USERS_PROFILE_DOCUMENT_NAME = "profile"

const val MESSAGES_COLLECTION_NAME = "messages"

const val CONVERSATION_COLLECTION_NAME = "conversation"

const val CHANGES_BRIEF_COLLECTION_NAME = "changes-brief"

enum class UserAttributeKeys(val value: String) {
    NAME("name"),
    EMAIL("email"),
    ABOUT("about"),
    CONTACTS("contacts"),
    IMAGE("image")
}

enum class MessageAttributeKeys(val value: String) {
    MESSAGE_ID("messageId"),
    SENDER("sender"),
    RECEIVER("receiver"),
    TIME("time"),
    VALUE("value"),
    MESSAGE_ATTRIBUTES("attributes"),
    VIEW_STATUS("view_status"),
    IS_UPDATED("is_updated"),
    IS_DELETED("is_deleted"),
    SENDER_REACTION("sender_reaction"),
    RECEIVER_REACTION("receiver_reaction")
}


enum class ConversationBriefAttributeKeys(val value: String) {
    NEW_MESSAGE_COUNT("new_message_count"),
    MESSAGE_ID("message_id"),
    SENDER_EMAIL("sender_email"),
    MESSAGE_VALUE("message_value"),
    TIME("time"),
    VIEW_STATUS("view_status")
}