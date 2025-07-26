package com.malakiapps.whatsappclone.domain.di

import com.malakiapps.whatsappclone.domain.managers.AuthenticationContextManager
import com.malakiapps.whatsappclone.domain.managers.ContactsManager
import com.malakiapps.whatsappclone.domain.managers.EventsManager
import com.malakiapps.whatsappclone.domain.managers.MessagesManager
import com.malakiapps.whatsappclone.domain.managers.UserManager
import com.malakiapps.whatsappclone.domain.use_cases.GetContactsUseCase
import com.malakiapps.whatsappclone.domain.use_cases.GetConversationUseCase
import com.malakiapps.whatsappclone.domain.use_cases.GetUserContactUseCase
import com.malakiapps.whatsappclone.domain.use_cases.InitialAuthenticationCheckUseCase
import com.malakiapps.whatsappclone.domain.use_cases.InitializeUserUseCase
import com.malakiapps.whatsappclone.domain.use_cases.MigrateToGoogleAccountUseCase
import com.malakiapps.whatsappclone.domain.use_cases.OnLoginUpdateAccountUseCase
import com.malakiapps.whatsappclone.domain.use_cases.SendMessageUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateMessagesUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateUserContactUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateUserDetailsUseCase
import com.malakiapps.whatsappclone.presentation.view_models.MainViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


expect val platformModule: Module

val sharedModule = module {
    //Use cases
    singleOf(::InitializeUserUseCase)
    singleOf(::AuthenticationContextManager)
    singleOf(::GetUserContactUseCase)
    singleOf(::InitialAuthenticationCheckUseCase)

    singleOf(::OnLoginUpdateAccountUseCase)
    singleOf(::UpdateUserContactUseCase)
    singleOf(::UpdateUserDetailsUseCase)

    singleOf(::GetContactsUseCase)

    singleOf(::GetConversationUseCase)
    singleOf(::SendMessageUseCase)
    singleOf(::UpdateMessagesUseCase)

    singleOf(::MigrateToGoogleAccountUseCase)

    //Managers
    singleOf(::UserManager)
    singleOf(::EventsManager)
    singleOf(::AuthenticationContextManager)
    singleOf(::ContactsManager)
    singleOf(::MessagesManager)

    //Global View models
    singleOf(::MainViewModel)
}