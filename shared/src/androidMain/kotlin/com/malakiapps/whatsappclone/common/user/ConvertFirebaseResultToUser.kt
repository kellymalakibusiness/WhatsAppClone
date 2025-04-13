package com.malakiapps.whatsappclone.common.user

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun CancellableContinuation<User?>.handleOnCompleteSignIn(task: Task<AuthResult>) {
    val user = task.result.user?.let { user ->
        val id = user.uid
        val name = user.displayName ?: ""
        val email = user.email ?: ""
        val image = user.photoUrl

        if(email.isNotBlank() && id.isNotBlank()){
            User(
                id = user.uid,
                name = name,
                email = email,
                imageUri = image
            )
        } else {
            null
        }
    }
    resume(
        user,
        null
    )
}

fun CancellableContinuation<User?>.returnNull(){
    resume(null, null)
}