package com.malakiapps.whatsappclone.common.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malakiapps.whatsappclone.common.user.User
import com.malakiapps.whatsappclone.common.user.UserAuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AuthenticationViewModel(
    val authenticationRepository: UserAuthenticationRepository
): ViewModel() {

    private val _user: MutableStateFlow<User?> = MutableStateFlow(authenticationRepository.getCurrentUser())
    val user: StateFlow<User?> = _user

    fun signInWithGoogle(){
        viewModelScope.launch {
            authenticationRepository.signIn()
        }
    }

    fun updateProfile(name: String? = null, ){
        viewModelScope.launch {
            val result = authenticationRepository.updateProfile(
                name = name,
            )

            if(result){
                _user.value = authenticationRepository.getCurrentUser()
            }
        }
    }

    fun anonymousSignIn(){
        viewModelScope.launch {
            authenticationRepository.anonymousSignIn()
        }
    }
}