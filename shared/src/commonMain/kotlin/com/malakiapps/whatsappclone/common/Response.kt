package com.malakiapps.whatsappclone.common

sealed interface Response<out R, out E: Error> {
    data class Success<out R, out E: Error>(val data: R): Response<R, E>
    data class Failure<out R, out E: Error>(val error: E): Response<R, E>
}

fun <R, E: Error> Response<R, E>.getOrNull(): R? {
    return when(this){
        is Response.Failure<R, E> -> null
        is Response.Success<R, E> -> data
    }
}

fun <R, E: Error> Response<R, E>.onEach(success: (R) -> Unit = { _ -> }, failure: (E) -> Unit = { _ -> } ){
    when(this){
        is Response.Failure<R, E> -> failure(this.error)
        is Response.Success<R, E> -> success(this.data)
    }
}