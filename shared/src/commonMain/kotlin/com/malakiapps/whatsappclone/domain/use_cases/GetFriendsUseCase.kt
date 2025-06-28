package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.ForbiddenRequest
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.contacts.ContactsRepository
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.getOrNull

class GetFriendsUseCase(
    private val userManager: UserManager,
    private val contactsRepository: ContactsRepository
) {

    suspend operator fun invoke(emails: List<Email>): Response<List<Profile>, Error> {
        //First check if user is authenticated
        val userDetails = userManager.userDetailsState.value.getOrNull()

        return if(userDetails != null){
            contactsRepository.getContacts(emails)
        } else {
            Response.Failure(ForbiddenRequest)
        }
    }
}