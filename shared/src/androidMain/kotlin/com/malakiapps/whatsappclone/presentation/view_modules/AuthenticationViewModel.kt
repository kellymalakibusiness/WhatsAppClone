package com.malakiapps.whatsappclone.presentation.view_modules

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.malakiapps.whatsappclone.R
import com.malakiapps.whatsappclone.data.common.generateBase64ImageFromUrlUri
import com.malakiapps.whatsappclone.domain.common.AuthenticationError
import com.malakiapps.whatsappclone.domain.common.AuthenticationException
import com.malakiapps.whatsappclone.domain.common.AuthenticationUserNotFound
import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Event
import com.malakiapps.whatsappclone.domain.common.LoadingEvent
import com.malakiapps.whatsappclone.domain.common.NavigateToLogin
import com.malakiapps.whatsappclone.domain.common.NavigateToProfileInfo
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.handleOnFailureResponse
import com.malakiapps.whatsappclone.domain.common.onEachSuspending
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.AuthenticationRepository
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.SignInResponse
import com.malakiapps.whatsappclone.domain.user.UserType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class AuthenticationViewModel: ViewModel() {
    private val firebaseAuth = Firebase.auth
    private val _eventChannel = Channel<Event>()
    val eventsChannelFlow = _eventChannel.receiveAsFlow()

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            signIn(
                signInCall = {
                    try {
                        val credentialResponse = buildSignInCredentialManager(context)
                        credentialResponse.handleSignIn()
                    } catch (e: Exception) {
                        e.printStackTrace()

                        //Don't prevent the coroutine from being cancelled
                        if (e is CancellationException) {
                            throw e
                        }
                        Response.Failure(AuthenticationException(e.message ?: "Unknown error"))
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun anonymousSignIn() {
        viewModelScope.launch {
            signIn(
                signInCall = {
                    println("MALAKA: On anonymous sign in")
                    suspendCancellableCoroutine { cont ->
                        firebaseAuth.signInAnonymously()
                            .addOnCompleteListener { task ->
                                val authenticationContext = AuthenticationContext(
                                    name = Name("Anonymous User"),
                                    email = null,
                                    type = UserType.ANONYMOUS
                                )
                                cont.resume(Response.Success(SignInResponse(authenticationContext, null)), null)
                            }
                            .addOnFailureListener { error ->
                                cont.handleOnFailureResponse(error)
                            }
                    }
                }
            )
        }
    }

    fun logOut(context: Context){
        viewModelScope.launch {
            _eventChannel.send(LoadingEvent(true))
            signOut(context)
            _eventChannel.send(NavigateToLogin)
        }
    }

    private suspend fun signOut(context: Context) {
        val credentialManager = CredentialManager.Companion.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        firebaseAuth.signOut()
    }

    private suspend fun signIn(signInCall: suspend () -> Response<SignInResponse, Error>) {
        _eventChannel.send(LoadingEvent(true))

        val signInResponse = signInCall()

        _eventChannel.send(LoadingEvent(false))
        //React to the result from use case
        signInResponse.onEachSuspending(
            success = {
                _eventChannel.send(
                    NavigateToProfileInfo(
                        authenticationContext = it.authenticationContext,
                        initialImage = it.initialBase64ProfileImage
                    )
                )
            },
            failure = { error ->
                _eventChannel.send(
                    OnError(error)
                )
            }
        )
    }

    private suspend fun buildSignInCredentialManager(context: Context): GetCredentialResponse {
        val credentialManager = CredentialManager.Companion.create(context)
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .setAutoSelectEnabled(false)
                    .build()
            )
            .build()

        return credentialManager.getCredential(
            request = request,
            context = context
        )
    }

    private suspend fun GetCredentialResponse.handleSignIn(): Response<SignInResponse, AuthenticationError> {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val tokenCredential = GoogleIdTokenCredential.Companion.createFrom(credential.data)

                val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                return authResult.user?.let { currentUser ->
                    val authenticationContext = AuthenticationContext(
                        name = Name(currentUser.displayName ?: ""),
                        email = Email(currentUser.email ?: ""),
                        type = UserType.REAL
                    )
                    val initialImage = currentUser.photoUrl?.generateBase64ImageFromUrlUri()
                    Response.Success(
                        SignInResponse(
                            authenticationContext = authenticationContext,
                            initialBase64ProfileImage = initialImage
                        )
                    )
                } ?: Response.Failure(AuthenticationUserNotFound)
            } catch (e: GoogleIdTokenParsingException) {
                return Response.Failure(AuthenticationException(e.message ?: "Unknown error"))
            }
        } else {
            return Response.Failure(AuthenticationException("Incorrect credential"))
        }
    }
}