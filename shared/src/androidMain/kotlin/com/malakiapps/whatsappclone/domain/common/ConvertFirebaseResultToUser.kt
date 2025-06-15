package com.malakiapps.whatsappclone.domain.common

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.SignInResponse
import com.malakiapps.whatsappclone.domain.user.UserType
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun CancellableContinuation<Response<SignInResponse, AuthenticationError>>.handleOnCompleteSignIn(
    task: Task<AuthResult>,
    initialBase64Image: String?
) {
    val result: Response<SignInResponse, AuthenticationError> = task.result.user?.let { user ->
        val name = user.displayName ?: ""
        val email = user.email

        val authenticationUser = AuthenticationUser(
            name = name,
            email = email,
            type = UserType.REAL
        )
        Response.Success(
            SignInResponse(
                authenticationUser = authenticationUser,
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