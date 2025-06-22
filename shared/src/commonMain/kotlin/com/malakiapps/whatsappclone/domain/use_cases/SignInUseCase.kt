package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.SignInResponse
import com.malakiapps.whatsappclone.domain.user.AuthenticationRepository

class SignInUseCase(
    val authenticationRepository: AuthenticationRepository
) {

    suspend fun signInWithGoogle(): Response<SignInResponse, Error> {
        return authenticationRepository.signIn()
    }

    suspend fun signInAnonymously(): Response<SignInResponse, Error> {
        val response = authenticationRepository.anonymousSignIn()

        return when(response){
            is Response.Failure<AuthenticationContext, Error> -> Response.Failure(response.error)
            is Response.Success<AuthenticationContext, Error> -> {
                Response.Success(
                    SignInResponse(
                        authenticationContext = response.data,
                        initialBase64ProfileImage = null
                    )
                )
            }
        }

    }
}