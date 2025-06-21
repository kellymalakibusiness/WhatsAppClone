package com.malakiapps.whatsappclone.domain.user

data class UserUpdate (
    val email: Email,
    val name: ElementUpdateState<Name> = None,
    val about: ElementUpdateState<String> = None,
    val image: ElementUpdateState<Image?> = None,
    val addContact: ElementUpdateState<Email> = None,
    val removeContact: ElementUpdateState<Email> = None
)

sealed interface ElementUpdateState<out T>

data class Update<T>(val value: T): ElementUpdateState<T>

data object None: ElementUpdateState<Nothing>

fun ElementUpdateState<Name>.isNameUpdateValid(): Boolean {
    if (this is Update){
        val characterSize = this.value.value.replace(" ", "")

        return characterSize.isNotBlank() && characterSize.length < 30
    }
    return true
}

fun ElementUpdateState<Image?>.isImageUpdateValid(): Boolean {
    if (this is Update && this.value != null){
        return value.value.isNotBlank()
    }
    return true
}

fun ElementUpdateState<String>.isAboutUpdateValid(): Boolean {
    if (this is Update) {
        val characterSize = this.value.replace(" ", "")

        return characterSize.isNotBlank() && characterSize.length < 30
    }
    return true
}