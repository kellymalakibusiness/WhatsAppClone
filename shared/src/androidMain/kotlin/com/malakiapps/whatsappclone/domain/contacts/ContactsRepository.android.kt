package com.malakiapps.whatsappclone.domain.contacts

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Profile

actual interface ContactsRepository {
    actual suspend fun getContacts(emails: List<Email>): Response<List<Profile>, Error>
}