package com.malakiapps.whatsappclone.android.domain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import org.koin.compose.koinInject
import com.malakiapps.whatsappclone.android.presentation.compose.ComposeApp
import com.malakiapps.whatsappclone.android.domain.utils.compressImageToBase64
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.presentation.view_models.MainViewModel
import com.malakiapps.whatsappclone.presentation.view_modules.AuthenticationViewModel
import kotlinx.coroutines.flow.merge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FakeWhatsAppTheme {
                //Root view models
                val mainViewModel: MainViewModel = koinInject()
                val authenticationViewModel: AuthenticationViewModel = koinInject()

                val userState by mainViewModel.selfProfileState.collectAsState()
                ComposeApp(
                    rootEvents = merge(mainViewModel.eventsChannelFlow, authenticationViewModel.eventsChannelFlow),
                    profileState = userState,
                    convertUriToBase64Image = { imageUri ->
                        mainViewModel.setLoading(true)
                        val base64Image = compressImageToBase64(
                            image = imageUri,
                            contentResolver = contentResolver
                        )
                        mainViewModel.setLoading(false)
                        base64Image
                    },
                    generateBase64Image = { imageUri ->
                        mainViewModel.setLoading(true)
                        val base64Image = compressImageToBase64(
                            image = imageUri,
                            contentResolver = contentResolver
                        )
                        mainViewModel.setLoading(false)
                        base64Image
                    },
                    signInWithGoogle = {
                        authenticationViewModel.signInWithGoogle(this)
                    },
                    anonymousSignIn = {
                        authenticationViewModel.anonymousSignIn()
                    },
                    onLogOut = {
                        authenticationViewModel.logOut(this)
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
