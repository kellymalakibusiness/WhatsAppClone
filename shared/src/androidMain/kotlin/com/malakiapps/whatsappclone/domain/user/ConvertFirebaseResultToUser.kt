package com.malakiapps.whatsappclone.domain.user

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.malakiapps.whatsappclone.common.AuthenticationError
import com.malakiapps.whatsappclone.common.AuthenticationException
import com.malakiapps.whatsappclone.common.AuthenticationUserNotFound
import com.malakiapps.whatsappclone.common.Response
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