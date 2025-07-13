package com.malakiapps.whatsappclone.domain.managers

import com.malakiapps.whatsappclone.domain.messages.Conversation
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.getOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessagesManager(
    authenticationContextManager: AuthenticationContextManager,
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val _authenticationContext = authenticationContextManager.authenticationContextState
    private val _conversations: MutableStateFlow<Map<Email, Conversation>> = MutableStateFlow(emptyMap())
    val conversations: StateFlow<Map<Email, Conversation>> = _conversations

    //Begin the check for the new messages
    init {
        scope.launch {

        }
    }

    suspend fun listenForConversationChanges(){
        TODO()
    }

    private fun getAuthenticatedUserEmail(): Email? {
        return _authenticationContext.value.getOrNull()?.email
    }
}