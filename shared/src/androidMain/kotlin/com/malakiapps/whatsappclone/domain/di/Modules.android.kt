package com.malakiapps.whatsappclone.domain.di

import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.malakiapps.whatsappclone.data.AnonymousLocalUserStorageRepository
import com.malakiapps.whatsappclone.data.FirebaseFirestoreUserStorageRepository
import com.malakiapps.whatsappclone.data.FirebaseGoogleSignInAuthenticationRepository
import com.malakiapps.whatsappclone.data.FirebaseLocalSignInAuthenticationRepository
import com.malakiapps.whatsappclone.data.room.LocalUserDatabase
import com.malakiapps.whatsappclone.domain.user.AuthenticationRepository
import com.malakiapps.whatsappclone.domain.user.UserStorageRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
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

    single<UserStorageRepository>(named("authenticated_user_repository")){
        FirebaseFirestoreUserStorageRepository()
    }.bind<UserStorageRepository>()

    single<UserStorageRepository>(named("anonymous_user_repository")) {
        AnonymousLocalUserStorageRepository(get<LocalUserDatabase>().dao)
    }.bind<UserStorageRepository>()
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