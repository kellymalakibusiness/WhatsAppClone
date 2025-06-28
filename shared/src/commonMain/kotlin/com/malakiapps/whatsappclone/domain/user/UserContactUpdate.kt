package com.malakiapps.whatsappclone.domain.user

data class UserContactUpdate (
    val email: Email,
    val name: ElementUpdateState<Name> = None,
    val about: ElementUpdateState<About> = None,
    val image: ElementUpdateState<Image?> = None,
)

sealed interface ElementUpdateState<out T>

data class Some<T>(val value: T): ElementUpdateState<T>

data object None: ElementUpdateState<Nothing>

fun ElementUpdateState<Name>.isNameUpdateValid(): Boolean {
    if (this is Some){
        val characterSize = this.value.value.replace(" ", "")

        return characterSize.isNotBlank() && characterSize.length < 30
    }
    return true
}

fun ElementUpdateState<Image?>.isImageUpdateValid(): Boolean {
    if (this is Some && this.value != null){
        return value.value.isNotBlank()
    }
    return true
}

fun ElementUpdateState<About>.isAboutUpdateValid(): Boolean {
    if (this is Some) {
        val characterSize = this.value.value.replace(" ", "")

        return characterSize.isNotBlank() && characterSize.length < 30
    }
    return true
}