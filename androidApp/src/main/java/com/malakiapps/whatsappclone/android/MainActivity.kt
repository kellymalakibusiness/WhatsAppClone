package com.malakiapps.whatsappclone.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.coroutineScope
import com.malakiapps.whatsappclone.android.domain.compressImageToBase64
import com.malakiapps.whatsappclone.domain.user.UserAuthenticationRepository
import com.malakiapps.whatsappclone.view_models.AuthenticationViewModel
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject
import com.malakiapps.whatsappclone.android.compose.ComposeApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authenticationRepository: UserAuthenticationRepository by inject()
        authenticationRepository.initializeCredentialManager(context = this)

        setContent {
            FakeWhatsAppTheme {
                val authenticationViewModel: AuthenticationViewModel = koinInject()
                val shimmerEffect by authenticationViewModel.dashboardShimmerState.collectAsState()
                val userState by authenticationViewModel.userState.collectAsState()

                ComposeApp(
                    eventChannel = authenticationViewModel.eventsChannelFlow,
                    userState = userState,
                    dashboardShimmerEffect = shimmerEffect,
                    dashboardOnSignInWithGoogleClick = {
                        authenticationViewModel.signInWithGoogle()
                    },
                    dashboardOnContinueWithoutSignInClick = {
                        authenticationViewModel.anonymousSignIn()
                    },
                    profileOnContinueClick = { email, name, image ->
                        lifecycle.coroutineScope.launch {
                            authenticationViewModel.setLoading(true)
                            val base64Image = image?.let {
                                compressImageToBase64(it)
                            }
                            authenticationViewModel.initialUpdateUserProfile(
                                name = name,
                                image = base64Image,
                                email = email
                            )
                        }
                    },
                    onLogOut = {
                        authenticationViewModel.logOut()
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
