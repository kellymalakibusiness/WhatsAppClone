package com.malakiapps.whatsappclone.domain.common

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.SignInResponse
import com.malakiapps.whatsappclone.domain.user.UserType
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun CancellableContinuation<Response<SignInResponse, AuthenticationError>>.handleOnCompleteSignIn(
    task: Task<AuthResult>,
    initialBase64Image: Image?
) {
    val result: Response<SignInResponse, AuthenticationError> = task.result.user?.let { user ->
        val name = Name(user.displayName ?: "")
        val email = user.email?.let { Email(it) }

        val authenticationContext = AuthenticationContext(
            name = name,
            email = email,
            type = UserType.REAL
        )
        Response.Success(
            SignInResponse(
                authenticationContext = authenticationContext,
                initialBase64ProfileImage = initialBase64Image
            )
        )
    } ?: Response.Failure(AuthenticationUserNotFound)

    resume(
        result,
        null
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T>CancellableContinuation<Response<T, AuthenticationError>>.handleOnFailureResponse(ex: Exception) {
    resume(Response.Failure(AuthenticationException(ex.message ?: "Unknown error")), null)
}