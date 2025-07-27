package com.malakiapps.whatsappclone.domain.managers

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.GetUserError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.getOrNull
import com.malakiapps.whatsappclone.domain.common.loggerTag2
import com.malakiapps.whatsappclone.domain.use_cases.GetContactsUseCase
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class ContactsManager(
    private val getContactsUseCase: GetContactsUseCase
) {
    private val _contacts: MutableStateFlow<Map<Email, Profile>> = MutableStateFlow(emptyMap())
    val contacts: StateFlow<Map<Email, Profile>> = _contacts


    suspend fun getFriendsContacts(emails: List<Email>): Response<List<Profile>, Error> {
        //First we check in the stateflow for the case we had already read it
        //If we miss it, we check on the usecase
        val foundOnStateFlow = mutableListOf<Profile>()
        val missingFromStateFlow = mutableListOf<Email>()

        emails.forEach { eachEmail ->
            val foundResult = contacts.value[eachEmail]
            if (foundResult != null){
                //We got it from our state
                foundOnStateFlow.add(foundResult)
            } else {
                //Result not found
                missingFromStateFlow.add(eachEmail)
            }
        }

        //If we missed some, check them on use case
        if(missingFromStateFlow.isNotEmpty()){
            loggerTag2.i { "On contacts we missed these $missingFromStateFlow" }
            val fromUseCaseContacts = getContactsUseCase.getListOfContacts(emails = missingFromStateFlow)

            when(fromUseCaseContacts){
                is Response.Failure<*, *> -> {
                    loggerTag2.i { "We failed on error of ${fromUseCaseContacts.error}" }
                    return fromUseCaseContacts
                }
                is Response.Success<List<Profile>, Error> -> {
                    //Now add the ones we just found to the stateFlowResults
                    foundOnStateFlow.addAll(fromUseCaseContacts.data)

                    //Then update our stateflow with the new contacts
                    updateContactsState(updates = fromUseCaseContacts.data)
                }
            }
        }

        return Response.Success(data = foundOnStateFlow)
    }

    fun updateContactsState(updates: List<Profile>){
        val updatedMap = _contacts.value.toMutableMap().apply {
            updates.forEach { eachContactUpdate ->
                this[eachContactUpdate.email] = eachContactUpdate
            }
        }

        _contacts.update { updatedMap }
    }

    fun listenToContactChanges(email: Email): Flow<Response<Profile, GetUserError>> {
        return getContactsUseCase.listenForContactsChanges(email = email)
            .map {
                it.getOrNull()?.let { onEachUpdate ->
                    _contacts.update { beforeMap ->
                        buildMap {
                            putAll(beforeMap)
                            put(onEachUpdate.email, onEachUpdate)
                        }
                    }
                }

                it
            }
    }
}