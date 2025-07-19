package com.malakiapps.whatsappclone.android.presentation.compose

import android.net.Uri
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.malakiapps.whatsappclone.android.presentation.compose.common.ErrorDialog
import com.malakiapps.whatsappclone.android.presentation.compose.common.LoadingDialog
import com.malakiapps.whatsappclone.android.presentation.compose.common.UpdatingDialog
import com.malakiapps.whatsappclone.android.domain.utils.ScreenError
import com.malakiapps.whatsappclone.android.domain.utils.getErrorMessageObject
import com.malakiapps.whatsappclone.android.presentation.compose.screens.AccountSettingsScreenRoute
import com.malakiapps.whatsappclone.android.presentation.compose.screens.ConversationScreenRoute
import com.malakiapps.whatsappclone.android.presentation.compose.screens.DashboardScreenRoute
import com.malakiapps.whatsappclone.android.presentation.compose.screens.SelectContactScreenRoute
import com.malakiapps.whatsappclone.android.presentation.compose.screens.LoginUpdateProfileScreenRoute
import com.malakiapps.whatsappclone.android.presentation.compose.screens.ScreenDestination
import com.malakiapps.whatsappclone.android.presentation.compose.screens.ProfileSettingsScreenRoute
import com.malakiapps.whatsappclone.android.presentation.compose.screens.SettingsProfileUpdateAboutScreenRoute
import com.malakiapps.whatsappclone.android.presentation.compose.screens.SettingsProfileUpdateNameScreenRoute
import com.malakiapps.whatsappclone.android.presentation.compose.screens.SettingsScreenRoute
import com.malakiapps.whatsappclone.android.presentation.compose.screens.LoginWelcomeScreenRoute
import com.malakiapps.whatsappclone.android.presentation.compose.screens.conversation_screen.ui.ConversationScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.dashboard.DashboardScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.dashboard.DashboardScreenType
import com.malakiapps.whatsappclone.android.presentation.compose.screens.login.LoginWelcomeScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.new_chat_screen.SelectContactScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.login_update_contact_screen.LoginUpdateProfileScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.SettingsScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.UserDetailsInfo.Companion.toUserDetailsInfo
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.account_screen.AccountSettingsScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.profile_screen.SettingsProfileScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.profile_screen.about_screen.ProfileAboutScreen
import com.malakiapps.whatsappclone.android.presentation.compose.screens.settings_screen.profile_screen.name_screen.ProfileNameScreen
import com.malakiapps.whatsappclone.domain.common.Event
import com.malakiapps.whatsappclone.domain.common.GoBackToDashboard
import com.malakiapps.whatsappclone.domain.common.LoadingEvent
import com.malakiapps.whatsappclone.domain.common.LogOut
import com.malakiapps.whatsappclone.domain.common.NavigateToDashboard
import com.malakiapps.whatsappclone.domain.common.NavigateToLogin
import com.malakiapps.whatsappclone.domain.common.NavigateToProfileInfo
import com.malakiapps.whatsappclone.domain.common.NavigationEvent
import com.malakiapps.whatsappclone.domain.common.OnError
import com.malakiapps.whatsappclone.domain.common.PlayMessageTone
import com.malakiapps.whatsappclone.domain.common.UpdatingEvent
import com.malakiapps.whatsappclone.domain.screens.MessageCard
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.UserType
import com.malakiapps.whatsappclone.presentation.view_models.ConversationViewModel
import com.malakiapps.whatsappclone.presentation.view_models.DashboardViewModel
import com.malakiapps.whatsappclone.presentation.view_models.LoginUpdateContactViewModel
import com.malakiapps.whatsappclone.presentation.view_models.SelectContactViewModel
import com.malakiapps.whatsappclone.presentation.view_models.UpdateUserProfileViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ComposeApp(
    profileState: Profile?,
    userType: UserType?,
    rootEvents: Flow<Event>,
    convertUriToBase64Image: suspend (Uri) -> Image?,
    generateBase64Image: suspend (Uri) -> Image?,
    signInWithGoogle: () -> Unit,
    switchFromAnonymousToGoogle: () -> Unit,
    anonymousSignIn: () -> Unit,
    onLogOut: () -> Unit,
    onPlayMessageTone: () -> Unit
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
    val viewModelEvents = Channel<Event>()

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            merge(rootEvents, viewModelEvents.receiveAsFlow()).collect { event ->
                //Remove the loading in the case of navigating to another screen
                if (event is NavigationEvent || event is OnError) {
                    isLoading = false
                    isUpdating = false
                }
                when (event) {
                    is NavigateToProfileInfo -> {
                        navController.navigateToOurPage(
                            LoginUpdateProfileScreenRoute(
                                email = event.authenticationContext.email?.value,
                                name = event.authenticationContext.name.value,
                                image = event.initialImage?.value
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
                            DashboardScreenRoute,
                            removeAllBackstackScreens = true
                        )
                    }

                    NavigateToLogin -> {
                        navController.navigateToOurPage(
                            LoginWelcomeScreenRoute,
                            removeAllBackstackScreens = true
                        )
                    }

                    is UpdatingEvent -> {
                        isUpdating = event.isUpdating
                    }

                    is LogOut -> {
                        onLogOut()
                    }

                    PlayMessageTone -> onPlayMessageTone()
                    GoBackToDashboard -> {
                        navController.popBackStack(
                            route = DashboardScreenRoute,
                            inclusive = false
                        )
                    }
                }
            }
        }
    }

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = DashboardScreenRoute
        ) {
            composable<LoginWelcomeScreenRoute> {
                LoginWelcomeScreen(
                    onContinueWithGoogleClick = {
                        signInWithGoogle()
                    },
                    onContinueWithoutSigningInClick = {
                        anonymousSignIn()
                    }
                )
            }

            composable<LoginUpdateProfileScreenRoute> { backStackEntry ->
                val data = requireNotNull(backStackEntry.toRoute<LoginUpdateProfileScreenRoute>())
                val name = Name(data.name)
                val image = data.image?.let { Image(it) }
                val email = data.email?.let { Email(it) }

                val loginUpdateContactViewModel : LoginUpdateContactViewModel = koinViewModel<LoginUpdateContactViewModel>()

                LaunchedEffect(true) {
                    loginUpdateContactViewModel.eventsChannelFlow.collect {
                        viewModelEvents.send(it)
                    }
                }

                LoginUpdateProfileScreen(
                    profileState = profileState,
                    initialName = name,
                    initialBase64Image = image,
                    convertUriToBase64Image = convertUriToBase64Image,
                    onStartClick = { selectedName, selectedImage ->
                        loginUpdateContactViewModel.updateUserProfile(email = email, name = selectedName, image = selectedImage)
                    }
                )
            }

            composable<DashboardScreenRoute> {
                val dashboardConversationViewModel = koinViewModel<DashboardViewModel>()

                LaunchedEffect(true) {
                    dashboardConversationViewModel.eventsChannelFlow.collect {
                        viewModelEvents.send(it)
                    }
                }

                val conversations by dashboardConversationViewModel.chatsScreenConversationRow.collectAsState()
                DashboardScreen(
                    conversations = conversations,
                    userType = userType,
                    onPrimaryFloatingButtonPress = { dashboardScreenType ->
                        when(dashboardScreenType){
                            DashboardScreenType.CHATS -> {
                                navController.navigateToOurPage(SelectContactScreenRoute)
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
                            //TODO(Do create update stuff)
                        }
                    },
                    openConversation = {
                        navController.navigateToOurPage(ConversationScreenRoute(it.value))
                    },
                    openSettings = {
                        navController.navigateToOurPage(SettingsScreenRoute)
                    }
                )
            }

            composable<ConversationScreenRoute>(
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "fakeWhatsapp://conversation/{email}"
                    }
                )
            ) { backStackEntry ->
                val email = Email(requireNotNull(backStackEntry.toRoute<ConversationScreenRoute>()).email)
                val conversationViewModel = koinViewModel<ConversationViewModel>{ parametersOf(email) }

                val messages: List<MessageCard>? by conversationViewModel.conversation.collectAsState()
                val target by conversationViewModel.targetContact.collectAsState()

                LaunchedEffect(true) {
                    conversationViewModel.eventsChannelFlow.collect {
                        viewModelEvents.send(it)
                    }
                }

                ConversationScreen(
                    messages = messages,
                    onSendMessage = {
                        conversationViewModel.sendMessage(it)
                    },
                    onProfileClick = {

                    },
                    target = target,
                    onBackPress = {
                        navController.navigateUp()
                    },
                )
            }

            composable<SettingsScreenRoute>(
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
                    userDetailsInfo = profileState?.toUserDetailsInfo(),
                    userType = userType,
                    onSignInWithGoogleClick = switchFromAnonymousToGoogle,
                    sharedElementModifier = sharedElementModifier,
                    onProfileClick = {
                        navController.navigateToOurPage(ProfileSettingsScreenRoute)
                    },
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onAccountSettingsClick = {
                        navController.navigateToOurPage(AccountSettingsScreenRoute)
                    }
                )
            }

            composable<AccountSettingsScreenRoute>(
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

            composable<ProfileSettingsScreenRoute> {
                val sharedElementModifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = "profileImage"),
                        animatedVisibilityScope = this
                    )

                val imageProcessingScope = rememberCoroutineScope()

                val updateUserProfileViewModel = koinViewModel<UpdateUserProfileViewModel>()

                LaunchedEffect(true) {
                    updateUserProfileViewModel.eventsChannelFlow.collect {
                        viewModelEvents.send(it)
                    }
                }

                SettingsProfileScreen(
                    userDetailsInfo = profileState?.toUserDetailsInfo(),
                    sharedElementModifier = sharedElementModifier,
                    onBackPress = {
                        navController.navigateUp()
                    },
                    onNamePress = {
                        navController.navigateToOurPage(SettingsProfileUpdateNameScreenRoute)
                    },
                    onAboutPress = {
                        navController.navigateToOurPage(SettingsProfileUpdateAboutScreenRoute)
                    },
                    onImageUpdate = { imageUri ->
                        imageProcessingScope.launch {
                            val processedImage = generateBase64Image(imageUri)
                            updateUserProfileViewModel.updateUserImage(processedImage)
                        }
                    },
                )
            }

            composable<SettingsProfileUpdateNameScreenRoute>(
                enterTransition = {
                    slideInHorizontally { it }
                },
                exitTransition = {
                    slideOutHorizontally { it }
                }
            ) { screenRootEntry ->
                val navBackStackEntry = remember(screenRootEntry) { navController.getBackStackEntry(ProfileSettingsScreenRoute) }
                val updateUserProfileViewModel = koinViewModel<UpdateUserProfileViewModel>(viewModelStoreOwner = navBackStackEntry)

                LaunchedEffect(true) {
                    updateUserProfileViewModel.eventsChannelFlow.collect {
                        viewModelEvents.send(it)
                    }
                }

                ProfileNameScreen(
                    name = profileState?.name,
                    onSaveClick = {
                        updateUserProfileViewModel.updateUserName(it)
                    },
                    onBackPress = {
                        navController.navigateUp()
                    },
                )
            }

            composable<SettingsProfileUpdateAboutScreenRoute>(
                enterTransition = {
                    slideInHorizontally { it }
                },
                exitTransition = {
                    slideOutHorizontally { it }
                }
            ) { screenRootEntry ->
                val navBackStackEntry = remember(screenRootEntry) { navController.getBackStackEntry(ProfileSettingsScreenRoute) }
                val updateUserProfileViewModel = koinViewModel<UpdateUserProfileViewModel>(viewModelStoreOwner = navBackStackEntry)

                LaunchedEffect(true) {
                    updateUserProfileViewModel.eventsChannelFlow.collect {
                        viewModelEvents.send(it)
                    }
                }

                ProfileAboutScreen(
                    currentValue = profileState?.about,
                    onSelect = {
                        updateUserProfileViewModel.updateUserAbout(it)
                    },
                    onBackPress = {
                        navController.navigateUp()
                    },
                )
            }

            composable<SelectContactScreenRoute>(
                enterTransition = {
                    slideInHorizontally { it }
                },
                exitTransition = {
                    slideOutHorizontally { it }
                }
            ) {
                val selectContactViewModel = koinViewModel<SelectContactViewModel>()

                LaunchedEffect(true) {
                    selectContactViewModel.eventsChannelFlow.collect {
                        viewModelEvents.send(it)
                    }
                }
                val contacts by selectContactViewModel.contacts.collectAsState()
                val searchResults by selectContactViewModel.searchResults.collectAsState()
                val isLoading by selectContactViewModel.isLoading.collectAsState()
                val helpMessage by selectContactViewModel.helpMessage.collectAsState()
                val selfProfile by selectContactViewModel.selfProfile.collectAsState()
                SelectContactScreen(
                    contacts = contacts,
                    searchResults = searchResults,
                    isLoading = isLoading,
                    helpMessage = helpMessage,
                    onSearchBarTextChange = {
                        selectContactViewModel.searchForContact(emailValue = it)
                    },
                    selfProfile = selfProfile,
                    onSelectContact = { email ->
                        navController.navigateToOurPage(
                            screenDestination = ConversationScreenRoute(email = email.value),
                            popUpToScreenDestination = SelectContactScreenRoute
                        )
                    },
                    onAddNewContact = { email ->
                        selectContactViewModel.addNewContact(email)
                    },
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
    removeAllBackstackScreens: Boolean = false,
    popUpToScreenDestination: ScreenDestination? = null
) {
    navigate(screenDestination) {
        launchSingleTop = true

        if (popUpToScreenDestination != null){
            popUpTo(popUpToScreenDestination){
                inclusive = true
            }
        } else if (removeAllBackstackScreens) {
            popUpTo(0) {
                inclusive = true
            }
        }
    }
}