package com.malakiapps.whatsappclone.domain.common

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.malakiapps.whatsappclone.domain.user.AuthenticationUser
import com.malakiapps.whatsappclone.domain.user.UserType
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun CancellableContinuation<Response<AuthenticationUser, AuthenticationError>>.handleOnCompleteSignIn(
    task: Task<AuthResult>
) {
    val result: Response<AuthenticationUser, AuthenticationError> = task.result.user?.let { user ->
        val name = user.displayName ?: ""
        val email = user.email
        val image = user.photoUrl

        Response.Success(
            AuthenticationUser(
                name = name,
                email = email,
                initialImage = image,
                type = UserType.REAL
            )
        )
    } ?: Response.Failure(AuthenticationUserNotFound)

    resume(
        result,
        null
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
fun CancellableContinuation<Response<AuthenticationUser, AuthenticationError>>.handleOnFailureResponse(ex: Exception) {
    resume(Response.Failure(AuthenticationException(ex.message ?: "Unknown error")), null)
}