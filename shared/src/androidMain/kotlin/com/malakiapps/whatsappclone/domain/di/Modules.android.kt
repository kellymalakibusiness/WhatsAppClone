package com.malakiapps.whatsappclone.domain.di

import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.malakiapps.whatsappclone.data.AnonymousLocalUserAccountRepository
import com.malakiapps.whatsappclone.data.AnonymousLocalUserMessageRepository
import com.malakiapps.whatsappclone.data.FirebaseFirestoreContactsRepository
import com.malakiapps.whatsappclone.data.FirebaseFirestoreMessagesRepository
import com.malakiapps.whatsappclone.data.FirebaseFirestoreUserAccountRepository
import com.malakiapps.whatsappclone.data.FirebaseGoogleSignInAuthenticationRepository
import com.malakiapps.whatsappclone.data.FirebaseLocalSignInAuthenticationRepository
import com.malakiapps.whatsappclone.data.room.LocalUserDatabase
import com.malakiapps.whatsappclone.domain.contacts.ContactsRepository
import com.malakiapps.whatsappclone.domain.messages.AnonymousUserMessageRepository
import com.malakiapps.whatsappclone.domain.messages.MessagesRepository
import com.malakiapps.whatsappclone.domain.user.AnonymousUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticatedUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticationRepository
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.presentation.view_models.ConversationViewModel
import com.malakiapps.whatsappclone.presentation.view_models.DashboardViewModel
import com.malakiapps.whatsappclone.presentation.view_models.LoginUpdateContactViewModel
import com.malakiapps.whatsappclone.presentation.view_models.SelectContactViewModel
import com.malakiapps.whatsappclone.presentation.view_models.UpdateUserProfileViewModel
import com.malakiapps.whatsappclone.presentation.view_modules.AuthenticationViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun Module.androidModules(appBuildMode: AppBuildMode){
    when(appBuildMode){
        AppBuildMode.LOCAL -> localModules()
        AppBuildMode.PROD -> prodModules()
    }

    //General dependencies
    //RoomDB
    single {
        Room.databaseBuilder(
            get(),
            LocalUserDatabase::class.java,
            "local_storage.db"
        ).build()
    }

    singleOf(::FirebaseFirestoreUserAccountRepository).bind<AuthenticatedUserAccountRepository>()

    single<AnonymousUserAccountRepository> {
        AnonymousLocalUserAccountRepository(get<LocalUserDatabase>().dao)
    }.bind<AnonymousUserAccountRepository>()

    single {
        AnonymousLocalUserMessageRepository(get<LocalUserDatabase>().messagesDao)
    }.bind<AnonymousUserMessageRepository>()

    singleOf(::FirebaseFirestoreContactsRepository).bind<ContactsRepository>()
    singleOf(::FirebaseFirestoreMessagesRepository).bind<MessagesRepository>()

    //Screen View models
    viewModelOf(::LoginUpdateContactViewModel)
    viewModelOf(::UpdateUserProfileViewModel)
    viewModelOf(::AuthenticationViewModel)
    viewModelOf(::SelectContactViewModel)
    viewModelOf(::DashboardViewModel)

    viewModel{ (userEmail: Email) ->
        ConversationViewModel(
            userManager = get(),
            messagesManager = get(),
            contactsManager = get(),
            targetEmail = userEmail,
            authenticationContextManager = get(),
            getConversationUseCase = get(),
            eventsManager = get()
        )
    }
}

fun Module.localModules(){
    //Activate the local server
    Firebase.auth.useEmulator("10.0.2.2", 9099)

    singleOf(::FirebaseLocalSignInAuthenticationRepository).bind<AuthenticationRepository>()
}

fun Module.prodModules(){
    singleOf(::FirebaseGoogleSignInAuthenticationRepository).bind<AuthenticationRepository>()
}


actual val platformModule: Module = module {
    androidModules(
        appBuildMode = AppBuildMode.PROD//.LOCAL
    )
}