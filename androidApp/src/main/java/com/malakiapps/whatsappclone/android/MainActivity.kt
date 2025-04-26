package com.malakiapps.whatsappclone.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.malakiapps.whatsappclone.android.screens.AccountSettingsScreenContext
import com.malakiapps.whatsappclone.android.screens.ConversationScreenContext
import com.malakiapps.whatsappclone.android.screens.DashboardScreenContext
import com.malakiapps.whatsappclone.android.screens.ProfileInfoScreenContext
import com.malakiapps.whatsappclone.android.screens.SettingsScreenContext
import com.malakiapps.whatsappclone.android.screens.WelcomeLoginScreenContext
import com.malakiapps.whatsappclone.android.screens.conversation_screen.data.LastMessageWas
import com.malakiapps.whatsappclone.android.screens.conversation_screen.data.ReceivedMessageItem
import com.malakiapps.whatsappclone.android.screens.conversation_screen.ui.ConversationScreen
import com.malakiapps.whatsappclone.android.screens.dashboard.DashboardScreen
import com.malakiapps.whatsappclone.android.screens.login.LoginScreen
import com.malakiapps.whatsappclone.android.screens.profile_info_screen.ProfileInfoScreen
import com.malakiapps.whatsappclone.android.screens.settings_screen.SettingsScreen
import com.malakiapps.whatsappclone.android.screens.settings_screen.UserDetailsInfo
import com.malakiapps.whatsappclone.android.screens.settings_screen.account_screen.AccountSettingsScreen
import com.malakiapps.whatsappclone.common.user.User
import com.malakiapps.whatsappclone.common.user.UserAuthenticationRepository
import com.malakiapps.whatsappclone.common.view_models.AuthenticationViewModel
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authenticationRepository: UserAuthenticationRepository by inject()
        authenticationRepository.initializeCredentialManager(context = this)

        setContent {
            FakeWhatsAppTheme {
                val navController = rememberNavController()
                val authenticationViewModel: AuthenticationViewModel = koinInject()
                val user by authenticationViewModel.user.collectAsState()
                NavHost(
                    navController = navController,
                    startDestination = if (user == null){
                        WelcomeLoginScreenContext
                    } else {
                        DashboardScreenContext
                    }
                ){
                    composable<WelcomeLoginScreenContext> {
                        LoginScreen(
                            onContinueWithGoogleClick = {
                                authenticationViewModel.signInWithGoogle()
                                navController.navigate(ProfileInfoScreenContext)
                            },
                            onContinueWithoutSigningInClick = {
                                authenticationViewModel.anonymousSignIn()
                                navController.navigate(ProfileInfoScreenContext)
                            }
                        )
                    }

                    composable<ProfileInfoScreenContext> { backStackEntry ->
                        val data = requireNotNull(backStackEntry.toRoute<ProfileInfoScreenContext>())
                        ProfileInfoScreen(
                            user = User(
                                id = data.id,
                                name = data.name,
                                email = data.email,
                                imageUri = user?.imageUri
                            ),
                            onStartClick = { nameToUpdate ->
                                authenticationViewModel.updateProfile(name = nameToUpdate)
                                navController.navigate(DashboardScreenContext)
                            }
                        )
                    }

                    composable<DashboardScreenContext> {
                        DashboardScreen(
                            openSettings = {
                                navController.navigate(SettingsScreenContext)
                            }
                        )
                    }

                    composable<ConversationScreenContext> {
                        ConversationScreen(
                            messageItems = listOf(
                                ReceivedMessageItem(
                                    message = "hey",
                                    time = "14:30",
                                    lastMessageWas = LastMessageWas.None
                                ),
                                ReceivedMessageItem(
                                    message = "i have been posting everyday but not yet",
                                    time = "14:30",
                                    lastMessageWas = LastMessageWas.RECEIVED
                                ),
                            )
                        )
                    }

                    composable<SettingsScreenContext>(
                        enterTransition = {
                            slideInHorizontally{ it }
                        },
                        exitTransition = {
                            slideOutHorizontally{ it }
                        }
                    ) {
                        SettingsScreen(
                            userDetailsInfo = UserDetailsInfo(
                                image = R.drawable.kevin_durant,
                                name = "Kevin Durant",
                                email = "kevindurant@gmail.com",
                                about = "I'm Kevin Durant"
                            ),
                            onNavigateBack = {
                                navController.navigateUp()
                            },
                            onAccountSettingsClick = {
                                navController.navigate(AccountSettingsScreenContext)
                            }
                        )
                    }

                    composable<AccountSettingsScreenContext>(
                        enterTransition = {
                            slideInHorizontally{ it }
                        },
                        exitTransition = {
                            slideOutHorizontally{ it }
                        }
                    ) {
                        AccountSettingsScreen(
                            onNavigateBack = {
                                navController.navigateUp()
                            },
                            onLogout = {

                            }
                        )
                    }

                }
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
