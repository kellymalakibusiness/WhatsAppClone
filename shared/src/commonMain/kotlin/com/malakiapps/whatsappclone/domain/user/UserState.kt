package com.malakiapps.whatsappclone.domain.user

sealed interface UserState<out S>

data object StateLoading: UserState<Nothing>

data class StateValue<out S>(val value: S): UserState<S>

fun <S>UserState<S>.getOrNull(): S? {
    return when(this){
        StateLoading -> null
        is StateValue<S> -> this.value
    }
}