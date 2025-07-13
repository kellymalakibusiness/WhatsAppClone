package com.malakiapps.whatsappclone.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.malakiapps.whatsappclone.data.common.getContactReference
import com.malakiapps.whatsappclone.data.common.toContact
import com.malakiapps.whatsappclone.domain.common.GetUserError
import com.malakiapps.whatsappclone.domain.common.QueryContactsError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.USERS_COLLECTION_NAME
import com.malakiapps.whatsappclone.domain.common.UnExpectedError
import com.malakiapps.whatsappclone.domain.common.UnknownError
import com.malakiapps.whatsappclone.domain.common.UserNotFound
import com.malakiapps.whatsappclone.domain.contacts.ContactsRepository
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Profile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseFirestoreContactsRepository : ContactsRepository {
    private val firestore = Firebase.firestore

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getContacts(emails: List<Email>): Response<List<Response<Profile, GetUserError>>, QueryContactsError> {
        return suspendCancellableCoroutine { cont ->
            firestore
                .queryContactsDocuments(
                    emails = emails,
                    onSuccess = {
                        val contactsProfiles = it.map {
                            it.toContact()
                        }
                        cont.resume(Response.Success(contactsProfiles), null)
                    },
                    onFailure = {
                        cont.resume(Response.Failure(UnknownError(it)), null)
                    }
                )
        }
    }

    override fun listenForContactChanges(email: Email): Flow<Response<Profile, GetUserError>> {
        return callbackFlow {
            firestore.getContactReference(email = email).addSnapshotListener { snaphotResponse, error ->
                if (error != null){
                    trySend(Response.Failure(UnExpectedError(error.message ?: error.cause?.message ?: "{No provided message}")))
                }

                if(snaphotResponse != null && snaphotResponse.exists()){
                    trySend(snaphotResponse.toContact())
                } else {
                    trySend(Response.Failure(UserNotFound))
                }
            }
        }
    }
}

private fun FirebaseFirestore.queryContactsDocuments(
    emails: List<Email>,
    onSuccess: (List<DocumentSnapshot>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    if (emails.size == 1) {
        getContactReference(emails.first())
            .get()
            .addOnSuccessListener { response ->
                onSuccess(listOf(response))
            }
            .addOnFailureListener {
                onFailure(it)
            }
    } else {
        collection(USERS_COLLECTION_NAME)
            .whereIn(FieldPath.documentId(), emails.map { it.value })
            .get()
            .addOnSuccessListener {
                onSuccess(it.documents)
            }
    }
}