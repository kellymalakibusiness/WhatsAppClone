package com.malakiapps.whatsappclone.domain.contacts

import com.malakiapps.whatsappclone.domain.common.GetUserError
import com.malakiapps.whatsappclone.domain.common.QueryContactsError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Profile
import kotlinx.coroutines.flow.Flow

expect interface ContactsRepository {
    suspend fun getContacts(emails: List<Email>): Response<List<Response<Profile, GetUserError>>, QueryContactsError>

    fun listenForContactChanges(email: Email): Flow<Response<Profile, GetUserError>>
}