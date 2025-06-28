package com.malakiapps.whatsappclone.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.contacts.ContactsRepository
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Profile

class FirebaseFirestoreContactsRepository: ContactsRepository {
    private val firestore = Firebase.firestore
    override suspend fun getContacts(emails: List<Email>): Response<List<Profile>, Error> {
        TODO()
    }
}