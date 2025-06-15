package com.malakiapps.whatsappclone.android.domain.utils

import com.malakiapps.whatsappclone.domain.common.AuthenticationException
import com.malakiapps.whatsappclone.domain.common.AuthenticationUserNotFound
import com.malakiapps.whatsappclone.domain.common.EmailNotFound
import com.malakiapps.whatsappclone.domain.common.UnknownError
import com.malakiapps.whatsappclone.domain.common.UpdateUserException
import com.malakiapps.whatsappclone.domain.common.UserNotFound
import com.malakiapps.whatsappclone.domain.common.UserParsingError
import com.malakiapps.whatsappclone.domain.common.Error

fun Error.getErrorMessageObject(): ScreenError {
    return when(this){
        is AuthenticationException -> {
            ScreenError(
                message = message,
                dismissButton = "Close"
            )
        }
        AuthenticationUserNotFound -> {
            ScreenError(
                message = "User account not found. Sign In to be able to access the services.",
                dismissButton = "Close"
            )
        }
        is UnknownError -> {
            ScreenError(
                message = exception.message ?: "An Unknown error occurred.",
                dismissButton = "Close"
            )
        }

        UserNotFound -> {
            ScreenError(
                message = "User account not found. Sign In to continue accessing.",
                dismissButton = "Close"
            )
        }
        is UserParsingError -> {
            ScreenError(
                message = "System error. Problem with account profile.",
                dismissButton = "Close"
            )
        }
        is UpdateUserException -> {
            ScreenError(
                message = "An error occurred while updating User Profile.",
                dismissButton = "Close"
            )
        }

        EmailNotFound -> {
            ScreenError(
                message = "User was authenticated but email wasn't found. Please try again",
                dismissButton = "Okay"
            )
        }
    }
}