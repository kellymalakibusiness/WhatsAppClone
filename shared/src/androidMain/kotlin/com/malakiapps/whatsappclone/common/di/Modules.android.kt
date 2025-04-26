package com.malakiapps.whatsappclone.common.di

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.malakiapps.whatsappclone.common.user.FirebaseGoogleSignInAuthenticationRepository
import com.malakiapps.whatsappclone.common.user.FirebaseLocalSignInAuthenticationRepository
import com.malakiapps.whatsappclone.common.user.UserAuthenticationRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun Module.androidModules(appBuildMode: AppBuildMode){
    when(appBuildMode){
        AppBuildMode.LOCAL -> localModules()
        AppBuildMode.PROD -> prodModules()
    }
}

fun Module.localModules(){
    //Activate the local server
    Firebase.auth.useEmulator("10.0.2.2", 9099)

    singleOf(::FirebaseLocalSignInAuthenticationRepository).bind<UserAuthenticationRepository>()
}

fun Module.prodModules(){
    singleOf(::FirebaseGoogleSignInAuthenticationRepository).bind<UserAuthenticationRepository>()
}


actual val platformModule: Module = module {
    androidModules(
        appBuildMode = AppBuildMode.PROD//.LOCAL
    )
    /*single {
        OkHttp.create()
    }
    single {
        GetDataStorePath(get()).getThePath()
    }
    single {
        LinkOpener(get())
    }
    single {
        androidContext() as MainActivity
    }
    single {
        ImageSelector(get())
    }*/
}