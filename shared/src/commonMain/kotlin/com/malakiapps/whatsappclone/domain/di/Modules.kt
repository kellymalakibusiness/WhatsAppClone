package com.malakiapps.whatsappclone.domain.di

import com.malakiapps.whatsappclone.domain.use_cases.InitializeUserUseCase
import com.malakiapps.whatsappclone.domain.use_cases.GetUserAuthenticationStateUseCase
import com.malakiapps.whatsappclone.domain.use_cases.GetUserUseCase
import com.malakiapps.whatsappclone.domain.use_cases.InitialAuthenticationCheckUseCase
import com.malakiapps.whatsappclone.domain.use_cases.LogoutUseCase
import com.malakiapps.whatsappclone.domain.use_cases.OnLoginUpdateAccountUseCase
import com.malakiapps.whatsappclone.domain.use_cases.SignInUseCase
import com.malakiapps.whatsappclone.domain.use_cases.UpdateUserUseCase
import com.malakiapps.whatsappclone.presentation.view_models.AuthenticationViewModel
import com.malakiapps.whatsappclone.presentation.view_models.UserViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module



expect val platformModule: Module

val sharedModule = module {
    //Use cases
    single {
        InitializeUserUseCase(
            anonymousUserStorageRepository = get(named("anonymous_user_repository")),
            userStorageRepository = get(named("authenticated_user_repository")),
        )
    }

    single {
        GetUserAuthenticationStateUseCase(
            authenticationRepository = get()
        )
    }

    single {
        GetUserUseCase(
            userStorageRepository = get(named("authenticated_user_repository")),
            anonymousUserStorageRepository = get(named("anonymous_user_repository"))
        )
    }

    single {
        InitialAuthenticationCheckUseCase()
    }

    single {
        LogoutUseCase(
            authenticationRepository = get()
        )
    }

    single {
        OnLoginUpdateAccountUseCase(
            userStorageRepository = get(named("authenticated_user_repository")),
            anonymousUserStorageRepository = get(named("anonymous_user_repository"))
        )
    }

    single {
        SignInUseCase(
            authenticationRepository = get()
        )
    }

    single {
        UpdateUserUseCase(
            anonymousUserStorageRepository = get(named("anonymous_user_repository")),
            userStorageRepository = get(named("authenticated_user_repository")),
        )
    }

    //View models
    singleOf(::AuthenticationViewModel)
    singleOf(::UserViewModel)
}