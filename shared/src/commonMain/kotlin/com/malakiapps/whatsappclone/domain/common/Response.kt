package com.malakiapps.whatsappclone.domain.common

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

suspend fun <R, E: Error> Response<R, E>.onEachSuspending(success: suspend (R) -> Unit = { _ -> }, failure: suspend (E) -> Unit = { _ -> } ){
    when(this){
        is Response.Failure<R, E> -> failure(this.error)
        is Response.Success<R, E> -> success(this.data)
    }
}

fun <R, E: Error> Response<R, E>.isSuccess(): Boolean {
    return this is Response.Success
}

fun <R, E: Error> Response<R, E>.isFailure(): Boolean {
    return this is Response.Failure
}