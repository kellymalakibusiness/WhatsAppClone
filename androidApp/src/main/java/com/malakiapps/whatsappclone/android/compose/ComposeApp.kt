package com.malakiapps.whatsappclone.android.compose

import android.content.Context
import android.net.Uri
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.malakiapps.whatsappclone.android.compose.common.LoadingDialog
import com.malakiapps.whatsappclone.android.domain.utils.ScreenError
import com.malakiapps.whatsappclone.android.domain.utils.getErrorMessageObject
import com.malakiapps.whatsappclone.android.compose.screens.AccountSettingsScreenContext
import com.malakiapps.whatsappclone.android.compose.screens.ConversationScreenContext
import com.malakiapps.whatsappclone.android.compose.screens.DashboardScreenContext
import com.malakiapps.whatsappclone.android.compose.screens.ProfileInfoScreenContext
import com.malakiapps.whatsappclone.android.compose.screens.ScreenDestination
import com.malakiapps.whatsappclone.android.compose.screens.SettingsScreenContext
import com.malakiapps.whatsappclone.android.compose.screens.WelcomeLoginScreenContext
import com.malakiapps.whatsappclone.android.compose.screens.conversation_screen.data.LastMessageWas
import com.malakiapps.whatsappclone.android.compose.screens.conversation_screen.data.ReceivedMessageItem
import com.malakiapps.whatsappclone.android.compose.screens.conversation_screen.ui.ConversationScreen
import com.malakiapps.whatsappclone.android.compose.screens.dashboard.DashboardScreen
import com.malakiapps.whatsappclone.android.compose.screens.login.LoginScreen
import com.malakiapps.whatsappclone.android.compose.screens.profile_info_screen.ProfileInfoScreen
import com.malakiapps.whatsappclone.android.compose.screens.settings_screen.SettingsScreen
import com.malakiapps.whatsappclone.android.compose.screens.settings_screen.UserDetailsInfo.Companion.toUserDetailsInfo
import com.malakiapps.whatsappclone.android.compose.screens.settings_screen.account_screen.AccountSettingsScreen
import com.malakiapps.whatsappclone.common.Event
import com.malakiapps.whatsappclone.common.LoadingEvent
import com.malakiapps.whatsappclone.common.NavigateToDashboard
import com.malakiapps.whatsappclone.common.NavigateToLogin
import com.malakiapps.whatsappclone.common.NavigateToProfileInfo
import com.malakiapps.whatsappclone.common.NavigationEvent
import com.malakiapps.whatsappclone.common.OnError
import com.malakiapps.whatsappclone.domain.user.User
import kotlinx.coroutines.flow.Flow

@Composable
fun Context.ComposeApp(userState: User?, eventChannel: Flow<Event>, dashboardShimmerEffect: Boolean, dashboardOnSignInWithGoogleClick: () -> Unit, dashboardOnContinueWithoutSignInClick: () -> Unit, profileOnContinueClick: (String?, String, Uri?) -> Unit, onLogOut: () -> Unit) {
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current
    var isLoading by rememberSaveable {
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
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = DashboardScreenContext
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
                shimmerState = dashboardShimmerEffect,
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
            SettingsScreen(
                userDetailsInfo = userState?.toUserDetailsInfo(),
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

    }

    //The universal dialogs
    //Error dialog first
    if(error != null){

    } else {
        //Other less important dialogs
        //Loading
        if(isLoading){
            LoadingDialog()
        }
    }
}

private fun NavController.navigateToOurPage(screenDestination: ScreenDestination, removeAllBackstackScreens: Boolean = false) {
    navigate(screenDestination){
        launchSingleTop = true

        if(removeAllBackstackScreens){
            popUpTo(0){
                inclusive = true
            }
        }
    }
}