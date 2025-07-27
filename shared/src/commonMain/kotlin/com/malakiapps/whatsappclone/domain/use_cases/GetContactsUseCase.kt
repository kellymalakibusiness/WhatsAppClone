package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.ForbiddenRequest
import com.malakiapps.whatsappclone.domain.common.GetUserError
import com.malakiapps.whatsappclone.domain.common.QueryContactsError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.contacts.ContactsRepository
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.getOrNull
import kotlinx.coroutines.flow.Flow

class GetContactsUseCase(
    private val userManager: UserManager,
    private val contactsRepository: ContactsRepository
) {

    suspend fun getListOfContacts(emails: List<Email>): Response<List<Profile>, QueryContactsError> {
        //First check if user is authenticated
        val userDetails = userManager.userDetailsState.value.getOrNull()

        return if(userDetails != null){
            when(val contactsResult = contactsRepository.getContacts(emails)){
                is Response.Failure<List<Response<Profile, GetUserError>>, QueryContactsError> -> Response.Failure(contactsResult.error)
                is Response.Success<List<Response<Profile, GetUserError>>, QueryContactsError> -> {
                    //For now just ignore broken users
                    val profiles = contactsResult.data.mapNotNull {
                        it.getOrNull()
                    }
                    Response.Success(profiles)
                }
            }
        } else {
            Response.Failure(ForbiddenRequest)
        }
    }

    fun listenForContactsChanges(email: Email): Flow<Response<Profile, GetUserError>> {
        return contactsRepository.listenForContactChanges(email)
    }
}