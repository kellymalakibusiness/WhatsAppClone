package com.malakiapps.whatsappclone.android.domain

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.android.domain.utils.compressImageToBase64
import com.malakiapps.whatsappclone.android.presentation.FakeWhatsAppTheme
import com.malakiapps.whatsappclone.android.presentation.compose.ComposeApp
import com.malakiapps.whatsappclone.domain.managers.EventsManager
import com.malakiapps.whatsappclone.presentation.view_models.MainViewModel
import com.malakiapps.whatsappclone.presentation.view_modules.AuthenticationViewModel
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0
    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        soundId = soundPool.load(this, R.raw.conversation_tone, 1)

        setContent {
            FakeWhatsAppTheme {
                //Root view models
                val mainViewModel: MainViewModel = koinInject()
                val authenticationViewModel: AuthenticationViewModel = koinInject()
                val events: EventsManager = koinInject()

                val userState by mainViewModel.selfProfileState.collectAsState()
                val userDetails by mainViewModel.userDetails.collectAsState()
                ComposeApp(
                    events = events.events.receiveAsFlow(),
                    profileState = userState,
                    userType = userDetails?.type,
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
                    switchFromAnonymousToGoogle = {
                        authenticationViewModel.fromAnonymousToLinkWithGoogle(this)
                    },
                    anonymousSignIn = {
                        authenticationViewModel.anonymousSignIn()
                    },
                    onLogOut = {
                        authenticationViewModel.logOut(this)
                    },
                    onPlayMessageTone = {
                        if (!isDeviceInSilentMode()) {
                            soundPool.setOnLoadCompleteListener { _, _, _ ->
                                soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                            }
                        }
                    },
                    onShowNotification = { messageNotification ->
                        TODO()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun isDeviceInSilentMode(): Boolean {
        return when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_SILENT, AudioManager.RINGER_MODE_VIBRATE -> true
            AudioManager.RINGER_MODE_NORMAL -> false
            else -> false
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
