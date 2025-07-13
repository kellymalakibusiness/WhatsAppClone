package com.malakiapps.whatsappclone.data.common

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.malakiapps.whatsappclone.domain.common.CONVERSATION_COLLECTION_NAME
import com.malakiapps.whatsappclone.domain.common.MESSAGES_COLLECTION_NAME
import com.malakiapps.whatsappclone.domain.user.Email

fun FirebaseFirestore.getConversationReference(owner: Email, target: Email): CollectionReference {
    return getContactReference(email = owner)
        .collection(MESSAGES_COLLECTION_NAME)
        .document(target.value)
        .collection(CONVERSATION_COLLECTION_NAME)
}