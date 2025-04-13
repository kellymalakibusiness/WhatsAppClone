package com.malakiapps.whatsappclone.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.malakiapps.whatsappclone.android.screens.CallScreenContext
import com.malakiapps.whatsappclone.android.screens.ChatScreenContext
import com.malakiapps.whatsappclone.android.screens.CommunitiesScreenContext
import com.malakiapps.whatsappclone.android.screens.ConversationScreenContext
import com.malakiapps.whatsappclone.android.screens.DashboardScreenContext
import com.malakiapps.whatsappclone.android.screens.ProfileInfoScreenContext
import com.malakiapps.whatsappclone.android.screens.SettingsScreenContext
import com.malakiapps.whatsappclone.android.screens.UpdatesScreenContext
import com.malakiapps.whatsappclone.android.screens.WelcomeLoginScreenContext
import com.malakiapps.whatsappclone.android.screens.calls_screen.CallRow
import com.malakiapps.whatsappclone.android.screens.calls_screen.CallType
import com.malakiapps.whatsappclone.android.screens.calls_screen.CallsScreen
import com.malakiapps.whatsappclone.android.screens.chat_screen.ChatScreen
import com.malakiapps.whatsappclone.android.screens.communities_screen.CommunitiesScreen
import com.malakiapps.whatsappclone.android.screens.communities_screen.CommunityGroup
import com.malakiapps.whatsappclone.android.screens.communities_screen.CommunityItem
import com.malakiapps.whatsappclone.android.screens.conversation_screen.data.LastMessageWas
import com.malakiapps.whatsappclone.android.screens.conversation_screen.data.ReceivedMessageItem
import com.malakiapps.whatsappclone.android.screens.conversation_screen.ui.ConversationScreen
import com.malakiapps.whatsappclone.android.screens.dashboard.DashboardScreenType
import com.malakiapps.whatsappclone.android.screens.login.LoginScreen
import com.malakiapps.whatsappclone.android.screens.profile_info_screen.ProfileInfoScreen
import com.malakiapps.whatsappclone.android.screens.settings_screen.SettingsScreen
import com.malakiapps.whatsappclone.android.screens.settings_screen.UserDetailsInfo
import com.malakiapps.whatsappclone.android.screens.updates_screen.UpdatesScreen
import com.malakiapps.whatsappclone.common.di.initKoin
import com.malakiapps.whatsappclone.common.user.User
import com.malakiapps.whatsappclone.common.view_models.AuthenticationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initKoin{
            androidContext(this@MainActivity)
        }
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
                        ProfileInfoScreenContext(
                            id = user!!.id,
                            name = user!!.name,
                            email = user!!.email,
                        )//Change to the dashboard
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
                                navController.navigate(ChatScreenContext)
                            }
                        )
                    }

                    composable<ChatScreenContext> {
                        ChatScreen(
                            onDashboardScreenChange = {
                                navController.navigate(it.toScreenContext())
                            },
                            onTopAppBarCamera = {},
                            onTopAppBarSearch = {},
                            onTopAppBarMore = {},
                            onMessageFilter = {},
                            onArchivedClick = {},
                            onAddMessageClick = {}
                        )
                    }

                    composable<CommunitiesScreenContext> {
                        CommunitiesScreen(
                            onDashboardScreenChange = {
                                navController.navigate(it.toScreenContext())
                            },
                            communities = listOf(
                                CommunityItem(
                                    name = "AT Community Broadcast",
                                    image = R.drawable.kevin_durant,
                                    groups = listOf(
                                        CommunityGroup(
                                            name = "AT Community",
                                            image = R.drawable.kevin_durant,
                                            lastMessage = "~User1: This message was deleted."
                                        ),
                                    )
                                ),
                                CommunityItem(
                                    name = "DITA",
                                    image = R.drawable.kevin_durant,
                                    groups = listOf(
                                        CommunityGroup(
                                            name = "MISADITA",
                                            image = R.drawable.kevin_durant,
                                            lastMessage = "~User3: Another Message"
                                        ),
                                    )
                                ),
                                CommunityItem(
                                    name = "Safari Computer Club",
                                    image = R.drawable.kevin_durant,
                                    groups = listOf(
                                        CommunityGroup(
                                            name = "Computing Systems and Hackathons",
                                            image = R.drawable.kevin_durant,
                                            lastMessage = "~User4: Joined using this groupd...."
                                        ),
                                    )
                                ),
                            )
                        )
                    }

                    composable<CallScreenContext> {
                        CallsScreen(
                            onDashboardScreenChange = {
                                navController.navigate(it.toScreenContext())
                            },
                            onAddCall = {},
                            calls = (0..3).map {
                                CallRow(
                                    name = "User 123",
                                    image = R.drawable.kevin_durant,
                                    date = "March 17, 11:07",
                                    callType = CallType.entries.random()
                                )
                            }
                        )
                    }

                    composable<UpdatesScreenContext> {
                        UpdatesScreen(
                            onDashboardScreenChange = {
                                navController.navigate(it.toScreenContext())
                            }, 
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

                    composable<SettingsScreenContext> {
                        SettingsScreen(
                            userDetailsInfo = UserDetailsInfo(
                                image = R.drawable.kevin_durant,
                                name = "Kevin Durant",
                                email = "kevindurant@gmail.com",
                                about = "I'm Kevin Durant"
                            ),
                            onNavigateBack = {}
                        )
                    }
                }
            }
        }
    }
}

private fun DashboardScreenType.toScreenContext(): DashboardScreenContext {
    return when(this){
        DashboardScreenType.CHATS -> ChatScreenContext
        DashboardScreenType.UPDATES -> UpdatesScreenContext
        DashboardScreenType.COMMUNITIES -> CommunitiesScreenContext
        DashboardScreenType.CALLS -> CallScreenContext
        DashboardScreenType.SETTINGS -> SettingsScreenContext
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
