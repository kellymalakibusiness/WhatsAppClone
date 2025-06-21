package com.malakiapps.whatsappclone.android.domain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.coroutineScope
import com.malakiapps.whatsappclone.domain.user.UserAuthenticationRepository
import com.malakiapps.whatsappclone.presentation.view_models.AuthenticationViewModel
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject
import com.malakiapps.whatsappclone.android.presentation.compose.ComposeApp
import com.malakiapps.whatsappclone.android.domain.utils.UserAboutUpdate
import com.malakiapps.whatsappclone.android.domain.utils.UserImageUpdate
import com.malakiapps.whatsappclone.android.domain.utils.UserNameUpdate
import com.malakiapps.whatsappclone.android.domain.utils.compressImageToBase64
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.domain.user.Initialized
import com.malakiapps.whatsappclone.presentation.view_models.ContactsViewModel
import com.malakiapps.whatsappclone.presentation.view_models.MessagesViewModel
import com.malakiapps.whatsappclone.presentation.view_models.UserViewModel
import kotlinx.coroutines.flow.merge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authenticationRepository: UserAuthenticationRepository by inject()
        authenticationRepository.initializeCredentialManager(context = this)

        setContent {
            FakeWhatsAppTheme {
                //Root view models
                val authenticationViewModel: AuthenticationViewModel = koinInject()
                val userViewModel: UserViewModel = koinInject()
                val contactsViewModel: ContactsViewModel = koinInject()
                val messagesViewModel: MessagesViewModel = koinInject()

                val authCtxState by authenticationViewModel.authenticationContextState.collectAsState()
                val userState by userViewModel.userState.collectAsState()

                //Listen for logout
                LaunchedEffect(authCtxState) {
                    //If not initialized, the initial check hasn't complete yet
                    if (authCtxState is Initialized) {
                        (authCtxState as Initialized).value?.let {
                            //User logged in
                            //Prepare all view models
                            userViewModel.initializeUserItem(it)
                            messagesViewModel.initializeMessages(it)
                        } ?: run {
                            //User logged out
                            authenticationViewModel.logOut()
                        }
                    }
                }

                //Listen for the user object
                LaunchedEffect(userState) {
                    userState?.let { availableUser ->
                        contactsViewModel.initializeContacts(availableUser)
                    }
                }

                ComposeApp(
                    eventChannel = merge(
                        authenticationViewModel.eventsChannelFlow,
                        userViewModel.eventsChannelFlow
                    ),
                    userState = userState,
                    dashboardOnSignInWithGoogleClick = {
                        authenticationViewModel.signInWithGoogle()
                    },
                    dashboardOnContinueWithoutSignInClick = {
                        authenticationViewModel.anonymousSignIn()
                    },
                    convertUriToBase64Image = { imageUri ->
                        authenticationViewModel.setLoading(true)
                        val base64Image = compressImageToBase64(
                            image = imageUri,
                            contentResolver = contentResolver
                        )
                        authenticationViewModel.setLoading(false)
                        base64Image
                    },
                    profileOnContinueClick = { email, name, image ->
                        userViewModel.initialUpdateUserProfile(
                            name = name,
                            image = image,
                            email = email
                        )
                    },
                    onLogOut = {
                        authenticationViewModel.logOut()
                    },
                    onUserUpdate = { userUpdate ->
                        when (userUpdate) {
                            is UserAboutUpdate -> {
                                userViewModel.updateUserAbout(about = userUpdate.value)
                            }

                            is UserImageUpdate -> {
                                //Update user profile picture
                                lifecycle.coroutineScope.launch {
                                    authenticationViewModel.setLoading(true)
                                    val base64Image = compressImageToBase64(
                                        image = userUpdate.image,
                                        contentResolver = contentResolver
                                    )
                                    authenticationViewModel.setLoading(false)
                                    userViewModel.updateUserImage(
                                        image = base64Image,
                                    )
                                }
                            }

                            is UserNameUpdate -> {
                                userViewModel.updateUserName(name = userUpdate.value)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    FakeWhatsAppTheme {
        GreetingView("Hello, Android!")
    }
}
