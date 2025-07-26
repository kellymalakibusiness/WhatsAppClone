package com.malakiapps.whatsappclone.data.common

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.malakiapps.whatsappclone.domain.common.GetUserError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.USERS_COLLECTION_NAME
import com.malakiapps.whatsappclone.domain.common.UserAttributeKeys
import com.malakiapps.whatsappclone.domain.common.UserParsingError
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Profile

fun DocumentSnapshot.toContact(): Response<Profile, GetUserError> {
    return data.let { document ->
        val profile = Profile(
            name = (document?.get(UserAttributeKeys.NAME.value) as? String)?.let { Name(it) } ?: return Response.Failure(
                UserParsingError(UserAttributeKeys.NAME.value)
            ),
            email = (document[UserAttributeKeys.EMAIL.value] as? String)?.let { Email(it) } ?: return Response.Failure(
                UserParsingError(UserAttributeKeys.EMAIL.value)
            ),
            about = (document[UserAttributeKeys.ABOUT.value] as? String
                ?: "Hey there! I'm using Fake WhatsApp.").let { About(it) },
            image = (document[UserAttributeKeys.IMAGE.value] as? String)?.let { Image(it) }
        )

        Response.Success(profile)
    }
}

fun FirebaseFirestore.getContactReference(email: Email): DocumentReference {
    return collection(USERS_COLLECTION_NAME).document(email.value)
}