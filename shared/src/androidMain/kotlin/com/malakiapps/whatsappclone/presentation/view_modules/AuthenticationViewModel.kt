package com.malakiapps.whatsappclone.presentation.view_modules

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.malakiapps.whatsappclone.R
import com.malakiapps.whatsappclone.data.common.generateBase64ImageFromUrlUri
import com.malakiapps.whatsappclone.domain.common.AuthenticationError
import com.malakiapps.whatsappclone.domain.common.AuthenticationException
import com.malakiapps.whatsappclone.domain.common.AuthenticationUserNotFound
import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.GoBackToDashboard
import com.malakiapps.whatsappclone.domain.common.LoadingEvent
import com.malakiapps.whatsappclone.domain.common.NavigateToProfileInfo
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.common.UserAccountAlreadyExistException
import com.malakiapps.whatsappclone.domain.common.handleOnFailureResponse
import com.malakiapps.whatsappclone.domain.managers.EventsManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.user.AuthenticationContext
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.SignInResponse
import com.malakiapps.whatsappclone.domain.user.UserType
import com.malakiapps.whatsappclone.domain.user.getOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class AuthenticationViewModel(
    private val userManager: UserManager,
    private val eventsManager: EventsManager
): ViewModel() {
    private val firebaseAuth = Firebase.auth

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            signIn(
                signInCall = {
                    //Response.Success(data = SignInResponse(authenticationContext = AuthenticationContext(name = Name("Kelly"), email = Email("kellygang6023@gmail.com"), type = UserType.REAL), initialBase64ProfileImage = null))
                    try {
                        val credentialResponse = buildSignInCredentialManager(context)
                        credentialResponse.handleSignIn()
                    } catch (e: Exception) {
                        e.printStackTrace()

                        //Don't prevent the coroutine from being cancelled
                        if (e is CancellationException) {
                            throw e
                        }
                        //Allow user to cancel too
                        if(e is GetCredentialCancellationException){
                            eventsManager.sendEvent(LoadingEvent(false))
                            cancel(CancellationException("User cancelled the flow"))
                            ensureActive()
                        }
                        Response.Failure(AuthenticationException(e.message ?: "Unknown error"))
                    }
                }
            )
        }
    }

    fun fromAnonymousToLinkWithGoogle(context: Context){
        viewModelScope.launch {
            eventsManager.sendEvent(LoadingEvent(true))

            try {
                val credentialResponse = buildSignInCredentialManager(context)
                val response = credentialResponse.handleUpgradeToGoogleFromAnonymous()

                //React to the result from use case
                when(response){
                    is Response.Failure<SignInResponse, AuthenticationError> -> {
                        eventsManager.sendEvent(
                            OnError(from = this@AuthenticationViewModel::class, error = response.error)
                        )
                    }
                    is Response.Success<SignInResponse, AuthenticationError> -> {
                        //Do our migration of the account
                        userManager.updateUserFromAnonymousAccount(response.data)
                        //Go back to the dashboard
                        eventsManager.sendEvent(GoBackToDashboard)
                    }
                }
            } catch (e: GetCredentialCancellationException){
            }
            eventsManager.sendEvent(LoadingEvent(false))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun anonymousSignIn() {
        viewModelScope.launch {
            signIn(
                signInCall = {
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
            eventsManager.sendEvent(LoadingEvent(true))
            signOut(context)
        }
    }

    private suspend fun signOut(context: Context) {
        val credentialManager = CredentialManager.Companion.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        if(userManager.userDetailsState.value.getOrNull()?.type == UserType.ANONYMOUS){
            //No point of logging out of anonymous account. No longer needed
            firebaseAuth.currentUser?.delete()
        } else {
            firebaseAuth.signOut()
        }
    }

    private suspend fun signIn(signInCall: suspend () -> Response<SignInResponse, Error>) {
        eventsManager.sendEvent(LoadingEvent(true))

        val signInResponse = signInCall()

        eventsManager.sendEvent(LoadingEvent(false))
        //React to the result from use case
        when(signInResponse){
            is Response.Failure<SignInResponse, Error> -> {
                eventsManager.sendEvent(
                    OnError(from = this@AuthenticationViewModel::class, error = signInResponse.error)
                )
            }
            is Response.Success<SignInResponse, Error> -> {
                eventsManager.sendEvent(
                    NavigateToProfileInfo(
                        authenticationContext = signInResponse.data.authenticationContext,
                        initialImage = signInResponse.data.initialBase64ProfileImage
                    )
                )
            }
        }
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

    private suspend fun GetCredentialResponse.handleUpgradeToGoogleFromAnonymous(): Response<SignInResponse, AuthenticationError> {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val tokenCredential = GoogleIdTokenCredential.Companion.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
                val authResult = firebaseAuth.currentUser?.linkWithCredential(authCredential)?.await()

                return authResult?.user?.let { currentUser ->
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
            } catch (e: FirebaseAuthUserCollisionException) {
                return Response.Failure(
                    UserAccountAlreadyExistException(
                        e.message ?: "Cannot link existing account to anonymous account"
                    )
                )
            }
        } else {
            return Response.Failure(AuthenticationException("Incorrect credential"))
        }
    }
}