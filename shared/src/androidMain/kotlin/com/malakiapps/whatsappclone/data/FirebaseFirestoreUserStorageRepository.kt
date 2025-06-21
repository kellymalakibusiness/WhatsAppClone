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
import com.malakiapps.whatsappclone.domain.common.UserAttributeKeys
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Update
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserStorageRepository
import com.malakiapps.whatsappclone.domain.user.UserType
import com.malakiapps.whatsappclone.domain.user.UserUpdate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseFirestoreUserStorageRepository : UserStorageRepository {
    val firestore = Firebase.firestore

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

    override suspend fun createUser(
        email: Email,
        authenticationContext: AuthenticationContext
    ): Response<User, CreateUserError> {
        val mapUser = authenticationContext.toCreateUserHashMap(actualEmail = email)
        val result: Response<Unit, CreateUserError> = suspendCancellableCoroutine { cont ->
            getUserReference(email = email)
                .set(mapUser, SetOptions.merge())
                .addOnCompleteListener {
                    cont.resume(Response.Success(Unit), null)
                }
                .addOnFailureListener { error ->
                    cont.resume(Response.Failure(UnknownError(error)), null)
                }
        }

        return result.getUpdateUser(email)

    }

    override suspend fun getUser(email: Email): Response<User, GetUserError> {
        return suspendCancellableCoroutine { cont ->
            getUserReference(email = email)
                .get(/*Source.CACHE*/)
                .addOnCompleteListener { response ->
                    cont.resume(response.toUser(), null)
                }
                .addOnFailureListener {
                    cont.resume(Response.Failure(UserNotFound), null)
                }
        }
    }

    override suspend fun updateUser(userUpdate: UserUpdate): Response<User, UpdateUserError> {
        val result: Response<Unit, UpdateUserError> = suspendCancellableCoroutine { cont ->
            val updateMap = userUpdate.toUpdateUserHashMap()

            getUserReference(email = userUpdate.email)
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

        return result.getUpdateUser(userUpdate.email)
    }

    override suspend fun deleteUser(email: Email): Response<Unit, DeleteUserError> {
        return suspendCancellableCoroutine { cont ->
            getUserReference(email = email)
                .delete()
                .addOnCompleteListener {
                    cont.resume(Response.Success(Unit), null)
                }
                .addOnFailureListener { error ->
                    cont.resume(Response.Failure(UnknownError(error)), null)
                }
        }
    }

    private suspend fun <E : Error> Response<Unit, E>.getUpdateUser(email: Email): Response<User, E> {
        return when (this) {
            is Response.Failure<Unit, E> -> Response.Failure(error)
            is Response.Success<Unit, E> -> {
                getUser(email).getOrNull()?.let { foundUser ->
                    Response.Success(data = foundUser)
                } ?: Response.Failure(UserNotFound as E)
            }
        }
    }

    private fun getUserReference(email: Email): DocumentReference {
        return firestore.collection(USERS_COLLECTION_NAME).document(email.value)
    }
}

private fun AuthenticationContext.toCreateUserHashMap(actualEmail: Email): HashMap<String, Any> {
    return hashMapOf(
        UserAttributeKeys.NAME to name,
        UserAttributeKeys.EMAIL to actualEmail,
    )
}

private fun Task<DocumentSnapshot>.toUser(): Response<User, GetUserError> {
    return result.data.let { document ->
        val user = User(
            name = (document?.get(UserAttributeKeys.NAME) as? String)?.let { Name(it) } ?: return Response.Failure(
                UserParsingError(UserAttributeKeys.NAME)
            ),
            email = (document[UserAttributeKeys.EMAIL] as? String)?.let { Email(it) } ?: return Response.Failure(
                UserParsingError(UserAttributeKeys.EMAIL)
            ),
            about = document[UserAttributeKeys.ABOUT] as? String
                ?: "Hey there! I'm using Fake WhatsApp.",
            image = (document[UserAttributeKeys.IMAGE] as? String)?.let { Image(it) },
            contacts = (document[UserAttributeKeys.CONTACTS] as? List<String>)?.let { response -> response.map { Email(it) } } ?: emptyList(),
            type = UserType.REAL
        )

        Response.Success(user)
    }
}

private fun UserUpdate.toUpdateUserHashMap(): Map<String, Any?> {
    return buildMap {
        if (name is Update) {
            put(UserAttributeKeys.NAME, name.value)
        }

        if (about is Update) {
            put(UserAttributeKeys.ABOUT, about.value)
        }

        if (image is Update) {
            put(UserAttributeKeys.IMAGE, image.value)
        }

        if (addContact is Update) {
            put(UserAttributeKeys.CONTACTS, FieldValue.arrayUnion(addContact.value))
        }

        if (removeContact is Update) {
            put(UserAttributeKeys.CONTACTS, FieldValue.arrayRemove(removeContact.value))
        }
    }
}