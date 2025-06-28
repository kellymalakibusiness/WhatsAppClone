package com.malakiapps.whatsappclone.domain.user

data class UserDetailsUpdate(
    val email: Email,
    val addContact: ElementUpdateState<Email> = None,
    val removeContact: ElementUpdateState<Email> = None
)

fun ElementUpdateState<Email>.isContactUpdateValid(): Boolean {
    if (this is Some){
        return this.value.value.isNotBlank() && this.value.value.contains('@')
    }
    return true
}