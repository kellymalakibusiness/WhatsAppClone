package com.malakiapps.whatsappclone.di

import com.malakiapps.whatsappclone.view_models.AuthenticationViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module



expect val platformModule: Module

val sharedModule = module {
    single {
        AuthenticationViewModel(
            authenticationRepository = get(),
            userStorageRepository = get(named("authenticated_user_repository")),
            anonymousUserStorageRepository = get(named("anonymous_user_repository"))
        )
    }
}