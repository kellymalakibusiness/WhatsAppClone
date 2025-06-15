package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.SignInResponse
import com.malakiapps.whatsappclone.domain.user.UserAuthenticationRepository

class SignInUseCase(
    val authenticationRepository: UserAuthenticationRepository
) {

    suspend fun signInWithGoogle(): Response<SignInResponse, Error> {
        return authenticationRepository.signIn()
    }

    suspend fun signInAnonymously(): Response<SignInResponse, Error> {
        val response = authenticationRepository.anonymousSignIn()

        return when(response){
            is Response.Failure<AuthenticationUser, Error> -> Response.Failure(response.error)
            is Response.Success<AuthenticationUser, Error> -> {
                Response.Success(
                    SignInResponse(
                        authenticationUser = response.data,
                        initialBase64ProfileImage = null
                    )
                )
            }
        }

    }
}