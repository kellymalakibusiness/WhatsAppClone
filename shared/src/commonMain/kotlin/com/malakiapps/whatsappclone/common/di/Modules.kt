package com.malakiapps.whatsappclone.common.di

import com.malakiapps.whatsappclone.common.view_models.AuthenticationViewModel
import com.malakiapps.whatsappclone.common.view_models.ProfileInfoViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module



expect val platformModule: Module

val sharedModule = module {
    singleOf(::AuthenticationViewModel)
    singleOf(::ProfileInfoViewModel)
    //Here we put the dependencies we would share between ios and android

    /*single {
        createHttpClient(get<HttpClientEngine>())
    }

    single {
        createDatastore(get())
    }

    singleOf(::QueryFactsUseCase)
    viewModelOf(::MainViewModel)
    singleOf(::ReadCurrentLanguageUseCase)
    singleOf(::HttpFactRepository).bind<FactRepository>()
    singleOf(::HttpCatImageRepository).bind<CatImageRepository>()*/

    //singleOf(::DummyFactRepository).bind<FactRepository>()
    //singleOf(::DummyImageRepository).bind<CatImageRepository>()
}