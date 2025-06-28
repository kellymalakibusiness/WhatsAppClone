package com.malakiapps.whatsappclone.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.malakiapps.whatsappclone.domain.common.CreateUserError
import com.malakiapps.whatsappclone.domain.common.DeleteUserError
import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.GetUserError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UnknownError
import com.malakiapps.whatsappclone.domain.common.UpdateUserError
import com.malakiapps.whatsappclone.domain.common.UpdateUserException
import com.malakiapps.whatsappclone.domain.common.UserNotFound
import com.malakiapps.whatsappclone.domain.common.UserParsingError
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.common.USERS_COLLECTION_NAME
import com.malakiapps.whatsappclone.domain.common.USERS_DETAILS_COLLECTION_NAME
import com.malakiapps.whatsappclone.domain.common.USERS_PROFILE_DOCUMENT_NAME
import com.malakiapps.whatsappclone.domain.common.UserAttributeKeys
import com.malakiapps.whatsappclone.domain.user.About
import com.malakiapps.whatsappclone.domain.user.AuthenticatedUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Some
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.UserType
import com.malakiapps.whatsappclone.domain.user.UserContactUpdate
import com.malakiapps.whatsappclone.domain.user.UserDetails
import com.malakiapps.whatsappclone.domain.user.UserDetailsUpdate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseFirestoreUserAccountRepository : AuthenticatedUserAccountRepository {
    private val firestore = Firebase.firestore

    init {
        //Make the cache size unlimited
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(
                PersistentCacheSettings.newBuilder()
                    .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build()
            )
            .build()
        firestore.firestoreSettings = settings
    }

    override suspend fun createContact(
        email: Email,
        authenticationContext: AuthenticationContext
    ): Response<Profile, CreateUserError> {
        val mapUser = authenticationContext.toCreateUserHashMap(actualEmail = email)
        val result: Response<Unit, CreateUserError> = suspendCancellableCoroutine { cont ->
            getContactReference(email = email)
                .set(mapUser, SetOptions.merge())
                .addOnCompleteListener {
                    cont.resume(Response.Success(Unit), null)
                }
                .addOnFailureListener { error ->
                    cont.resume(Response.Failure(UnknownError(error)), null)
                }
        }

        return result.getUpdatedContact(email)

    }

    override suspend fun getContact(email: Email): Response<Profile, GetUserError> {
        return suspendCancellableCoroutine { cont ->
            getContactReference(email = email)
                .get(/*Source.CACHE*/)
                .addOnCompleteListener { response ->
                    cont.resume(response.toContact(), null)
                }
                .addOnFailureListener {
                    cont.resume(Response.Failure(UserNotFound), null)
                }
        }
    }

    override suspend fun getUserDetails(email: Email): Response<UserDetails, GetUserError> {
        return suspendCancellableCoroutine { cont ->
            getUserDetailsReference(email = email)
                .get()
                .addOnCompleteListener { response ->
                    cont.resume(response.toUserDetails(), null)
                }
                .addOnFailureListener {
                    cont.resume(defaultUserDetails(), null)
                }
        }
    }

    override suspend fun updateContact(userContactUpdate: UserContactUpdate): Response<Profile, UpdateUserError> {
        val result: Response<Unit, UpdateUserError> = suspendCancellableCoroutine { cont ->
            val updateMap = userContactUpdate.toUpdateUserHashMap()

            getContactReference(email = userContactUpdate.email)
                .update(updateMap)
                .addOnCompleteListener {
                    cont.resume(Response.Success(Unit), null)
                }
                .addOnFailureListener { e ->
                    cont.resume(
                        Response.Failure(
                            UpdateUserException(
                                e.message ?: e.cause?.message ?: "No message provided"
                            )
                        ), null
                    )
                }
        }

        return result.getUpdatedContact(userContactUpdate.email)
    }

    override suspend fun updateUserDetails(userDetailsUpdate: UserDetailsUpdate): Response<UserDetails, UpdateUserError> {
        val result: Response<Unit, UpdateUserError> = suspendCancellableCoroutine { cont ->
            val updateMap = userDetailsUpdate.toUpdateUserDetailsHashMap()

            getUserDetailsReference(email = userDetailsUpdate.email)
                .update(updateMap)
                .addOnCompleteListener {
                    cont.resume(Response.Success(Unit), null)
                }
                .addOnFailureListener { e ->
                    cont.resume(
                        Response.Failure(
                            UpdateUserException(
                                e.message ?: e.cause?.message ?: "No message provided"
                            )
                        ), null
                    )
                }
        }

        return result.getUpdatedUserDetails(userDetailsUpdate.email)
    }

    override suspend fun deleteUser(email: Email): Response<Unit, DeleteUserError> {
        return suspendCancellableCoroutine { cont ->
            firestore.runBatch { batch ->
                batch.delete(getContactReference(email = email))
                batch.delete(getUserDetailsReference(email = email))
            }
                .addOnCompleteListener {
                    cont.resume(Response.Success(Unit), null)
                }
                .addOnFailureListener { e ->
                    //Could be a good error.Probably the user didn't have any contacts available. TODO("Improve the error handling by checking actual exception type")
                    cont.resume(Response.Success(Unit), null)
                }
        }
    }

    private suspend fun <E : Error> Response<Unit, E>.getUpdatedContact(email: Email): Response<Profile, E> {
        return when (this) {
            is Response.Failure<Unit, E> -> Response.Failure(error)
            is Response.Success<Unit, E> -> {
                getContact(email).getOrNull()?.let { foundUser ->
                    Response.Success(data = foundUser)
                } ?: Response.Failure(UserNotFound as E)
            }
        }
    }

    private suspend fun <E : Error> Response<Unit, E>.getUpdatedUserDetails(email: Email): Response<UserDetails, E> {
        return when (this) {
            is Response.Failure<Unit, E> -> Response.Failure(error)
            is Response.Success<Unit, E> -> {
                getUserDetails(email).getOrNull()?.let { foundUserDetails ->
                    Response.Success(data = foundUserDetails)
                } ?: Response.Failure(UserNotFound as E)
            }
        }
    }

    private fun getContactReference(email: Email): DocumentReference {
        return firestore.collection(USERS_COLLECTION_NAME).document(email.value)
    }

    private fun getUserDetailsReference(email: Email): DocumentReference {
        return getContactReference(email).collection(USERS_PROFILE_DOCUMENT_NAME).document(USERS_DETAILS_COLLECTION_NAME)
    }
}

private fun AuthenticationContext.toCreateUserHashMap(actualEmail: Email): HashMap<String, Any> {
    return hashMapOf(
        UserAttributeKeys.NAME to name,
        UserAttributeKeys.EMAIL to actualEmail,
    )
}

private fun Task<DocumentSnapshot>.toContact(): Response<Profile, GetUserError> {
    return result.data.let { document ->
        val profile = Profile(
            name = (document?.get(UserAttributeKeys.NAME) as? String)?.let { Name(it) } ?: return Response.Failure(
                UserParsingError(UserAttributeKeys.NAME)
            ),
            email = (document[UserAttributeKeys.EMAIL] as? String)?.let { Email(it) } ?: return Response.Failure(
                UserParsingError(UserAttributeKeys.EMAIL)
            ),
            about = (document[UserAttributeKeys.ABOUT] as? String
                ?: "Hey there! I'm using Fake WhatsApp.").let { About(it) },
            image = (document[UserAttributeKeys.IMAGE] as? String)?.let { Image(it) }
        )

        Response.Success(profile)
    }
}

private fun Task<DocumentSnapshot>.toUserDetails(): Response<UserDetails, GetUserError> {
    return result.data.let { document ->
        val userDetails = UserDetails(
            contacts = (document?.get(UserAttributeKeys.CONTACTS) as? List<String>)?.let { response -> response.map { Email(it) } } ?: emptyList(),
            type = UserType.REAL
        )

        Response.Success(userDetails)
    }
}

private fun defaultUserDetails(): Response<UserDetails, GetUserError> {
    return Response.Success(
        UserDetails(
            contacts = emptyList(),
            type = UserType.REAL
        )
    )
}

private fun UserContactUpdate.toUpdateUserHashMap(): Map<String, String?> {
    return buildMap {
        if (name is Some) {
            put(UserAttributeKeys.NAME, name.value.value)
        }

        if (about is Some) {
            put(UserAttributeKeys.ABOUT, about.value.value)
        }

        if (image is Some) {
            put(UserAttributeKeys.IMAGE, image.value?.value)
        }
    }
}

private fun UserDetailsUpdate.toUpdateUserDetailsHashMap(): Map<String, Any?> {
    return buildMap {
        if (addContact is Some) {
            put(UserAttributeKeys.CONTACTS, FieldValue.arrayUnion(addContact.value.value))
        }

        if (removeContact is Some) {
            put(UserAttributeKeys.CONTACTS, FieldValue.arrayRemove(removeContact.value.value))
        }
    }
}