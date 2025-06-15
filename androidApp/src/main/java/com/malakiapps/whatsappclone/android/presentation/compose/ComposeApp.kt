package com.malakiapps.whatsappclone.android.presentation.compose

import android.net.Uri
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.malakiapps.whatsappclone.android.presentation.compose.common.ErrorDialog
import com.malakiapps.whatsappclone.android.presentation.compose.common.LoadingDialog
import com.malakiapps.whatsappclone.android.presentation.compose.common.UpdatingDialog
import com.malakiapps.whatsappclone.android.domain.utils.ScreenError
import com.malakiapps.whatsappclone.android.domain.utils.getErrorMessageObject
import com.malakiapps.whatsappclone.android.presentation.compose.screens.AccountSettingsScreenContext
import com.malakiapps.whatsappclone.android.presentation.compose.screens.ConversationScreenContext
import com.malakiapps.whatsappclone.android.presentation.compose.screens.DashboardScreenContext
import com.malakiapps.whatsappclone.android.presentation.compose.screens.NewChatScreenContext
import com.malakiapps.whatsappclone.android.presentation.compose.screens.ProfileInfoScreenContext
import com.malakiapps.whatsappclone.android.presentation.compose.screens.ScreenDestination
import com.malakiapps.whatsappclone.android.presentation.compose.screens.ProfileSettingsScreenContext
import com.malakiapps.whatsappclone.android.presentation.compose.screens.SettingsProfileUpdateAboutScreenContext
import com.malakiapps.whatsappclone.android.presentation.compose.screens.SettingsProfileUpdateNameScreenContext
import com.malakiapps.whatsappclone.android.presentation.compose.screens.SettingsScreenContext
import com.malakiapps.whatsappclone.android.presentation.compose.screens.WelcomeLoginScreenContext
import com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.data.LastMessageWas
import com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.data.ReceivedMessageItem
import com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.ui.ConversationScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.dashboard.DashboardScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.dashboard.DashboardScreenType
import com.malakiapps.whatsappclone.android.presentation.compose.screens.login.LoginScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.new_chat_screen.NewChatScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.profile_info_screen.ProfileInfoScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.SettingsScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.UserDetailsInfo.Companion.toUserDetailsInfo
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.account_screen.AccountSettingsScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.profile_screen.SettingsProfileScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.profile_screen.about_screen.ProfileAboutScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.profile_screen.name_screen.ProfileNameScreen
import com.malakiapps.whatsappclone.android.domain.utils.UserAboutUpdate
import com.malakiapps.whatsappclone.android.domain.utils.UserImageUpdate
import com.malakiapps.whatsappclone.android.domain.utils.UserNameUpdate
import com.malakiapps.whatsappclone.android.domain.utils.UserUpdateType
import com.malakiapps.whatsappclone.domain.common.Event
import com.malakiapps.whatsappclone.domain.common.LoadingEvent
import com.malakiapps.whatsappclone.domain.common.LogOut
import com.malakiapps.whatsappclone.domain.common.NavigateToDashboard
import com.malakiapps.whatsappclone.domain.common.NavigateToLogin
import com.malakiapps.whatsappclone.domain.common.NavigateToProfileInfo
import com.malakiapps.whatsappclone.domain.common.NavigationEvent
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.UpdatingEvent
import com.malakiapps.whatsappclone.domain.user.User
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ComposeApp(
    userState: User?,
    eventChannel: Flow<Event>,
    dashboardOnSignInWithGoogleClick: () -> Unit,
    dashboardOnContinueWithoutSignInClick: () -> Unit,
    profileOnContinueClick: (String?, String, Uri?) -> Unit,
    onLogOut: () -> Unit,
    onUserUpdate: (UserUpdateType) -> Unit
) {
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current
    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }
    var isUpdating by remember {
        mutableStateOf(false)
    }
    var error: ScreenError? by rememberSaveable {
        mutableStateOf(null)
    }

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            eventChannel.collect { event ->
                //Remove the loading in the case of navigating to another screen
                if (event is NavigationEvent || event is OnError) {
                    isLoading = false
                    isUpdating = false
                }
                when (event) {
                    is NavigateToProfileInfo -> {
                        navController.navigateToOurPage(
                            ProfileInfoScreenContext(
                                email = event.authenticationUser.email,
                                name = event.authenticationUser.name,
                                image = event.authenticationUser.initialImage?.toString()
                            )
                        )
                    }

                    is OnError -> {
                        error = event.error.getErrorMessageObject()
                    }

                    is LoadingEvent -> {
                        isLoading = event.isLoading
                    }

                    is NavigateToDashboard -> {
                        navController.navigateToOurPage(
                            DashboardScreenContext,
                            removeAllBackstackScreens = true
                        )
                    }

                    NavigateToLogin -> {
                        navController.navigateToOurPage(
                            WelcomeLoginScreenContext,
                            removeAllBackstackScreens = true
                        )
                    }

                    is UpdatingEvent -> {
                        isUpdating = event.isUpdating
                    }

                    is LogOut -> {
                        onLogOut()
                    }
                }
            }
        }
    }

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = DashboardScreenContext//SettingsProfileUpdateAboutScreenContext//DashboardScreenContext
        ) {
            composable<WelcomeLoginScreenContext> {
                LoginScreen(
                    onContinueWithGoogleClick = {
                        dashboardOnSignInWithGoogleClick()
                    },
                    onContinueWithoutSigningInClick = {
                        dashboardOnContinueWithoutSignInClick()
                    }
                )
            }

            composable<ProfileInfoScreenContext> { backStackEntry ->
                val data = requireNotNull(backStackEntry.toRoute<ProfileInfoScreenContext>())

                ProfileInfoScreen(
                    isUserItemAvailable = userState != null,
                    initialName = data.name,
                    initialImage = data.image?.toUri(),
                    onStartClick = { name, image ->
                        profileOnContinueClick(data.email, name, image)
                    }
                )
            }

            composable<DashboardScreenContext> { backStackEntry ->
                //val data = requireNotNull(backStackEntry.toRoute<DashboardScreenContext>())
                DashboardScreen(
                    onPrimaryFloatingButtonPress = { dashboardScreenType ->
                        when(dashboardScreenType){
                            DashboardScreenType.CHATS -> {
                                navController.navigateToOurPage(NewChatScreenContext)
                            }
                            DashboardScreenType.UPDATES -> {
                                //TODO()
                            }
                            DashboardScreenType.COMMUNITIES -> {
                                //TODO()
                            }
                            DashboardScreenType.CALLS -> {
                                //TODO()
                            }
                        }
                    },
                    onSecondaryFloatingButtonPress = { dashboardScreenType ->
                        if(dashboardScreenType == DashboardScreenType.UPDATES){
                            //Do create update stuff
                        }
                    },
                    openSettings = {
                        navController.navigateToOurPage(SettingsScreenContext)
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
                    slideInHorizontally { it }
                },
                exitTransition = {
                    slideOutHorizontally { it }
                }
            ) {
                val sharedElementModifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = "profileImage"),
                        animatedVisibilityScope = this
                    )
                SettingsScreen(
                    userDetailsInfo = userState?.toUserDetailsInfo(),
                    sharedElementModifier = sharedElementModifier,
                    onProfileClick = {
                        navController.navigateToOurPage(ProfileSettingsScreenContext)
                    },
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onAccountSettingsClick = {
                        navController.navigateToOurPage(AccountSettingsScreenContext)
                    }
                )
            }

            composable<AccountSettingsScreenContext>(
                enterTransition = {
                    slideInHorizontally { it }
                },
                exitTransition = {
                    slideOutHorizontally { it }
                }
            ) {
                AccountSettingsScreen(
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onLogout = {
                        onLogOut()
                    }
                )
            }

            composable<ProfileSettingsScreenContext> {
                val sharedElementModifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = "profileImage"),
                        animatedVisibilityScope = this
                    )
                SettingsProfileScreen(
                    userDetailsInfo = userState?.toUserDetailsInfo(),
                    sharedElementModifier = sharedElementModifier,
                    onBackPress = {
                        navController.navigateUp()
                    },
                    onImageUpdate = { image ->
                        onUserUpdate(
                            UserImageUpdate(image)
                        )
                    },
                    onNamePress = {
                        navController.navigateToOurPage(SettingsProfileUpdateNameScreenContext)
                    },
                    onAboutPress = {
                        navController.navigateToOurPage(SettingsProfileUpdateAboutScreenContext)
                    }
                )
            }

            composable<SettingsProfileUpdateNameScreenContext>(
                enterTransition = {
                    slideInHorizontally { it }
                },
                exitTransition = {
                    slideOutHorizontally { it }
                }
            ) {
                ProfileNameScreen(
                    name = userState?.name,
                    onBackPress = {
                        navController.navigateUp()
                    },
                    onSaveClick = {
                        onUserUpdate(
                            UserNameUpdate(it)
                        )
                    }
                )
            }

            composable<SettingsProfileUpdateAboutScreenContext>(
                enterTransition = {
                    slideInHorizontally { it }
                },
                exitTransition = {
                    slideOutHorizontally { it }
                }
            ) {
                ProfileAboutScreen(
                    currentValue = userState?.about,
                    onBackPress = {
                        navController.navigateUp()
                    },
                    onSelect = {
                        onUserUpdate(
                            UserAboutUpdate(it)
                        )
                    },
                )
            }

            composable<NewChatScreenContext>(
                enterTransition = {
                    slideInHorizontally { it }
                },
                exitTransition = {
                    slideOutHorizontally { it }
                }
            ) {
                NewChatScreen(
                    contacts = emptyList(),
                    onBackPress = {
                        navController.navigateUp()
                    },

                )
            }


        }
    }

    //The universal dialogs
    //Error dialog first
    if (error != null) {
        ErrorDialog(
            screenError = error!!,
            onDismissDialog = {
                error = null
            }
        )
    } else {
        //Other less important dialogs
        //Loading
        if (isLoading) {
            LoadingDialog()
        }
        if (isUpdating) {
            UpdatingDialog()
        }
    }
}

private fun NavController.navigateToOurPage(
    screenDestination: ScreenDestination,
    removeAllBackstackScreens: Boolean = false
) {
    navigate(screenDestination) {
        launchSingleTop = true

        if (removeAllBackstackScreens) {
            popUpTo(0) {
                inclusive = true
            }
        }
    }
}