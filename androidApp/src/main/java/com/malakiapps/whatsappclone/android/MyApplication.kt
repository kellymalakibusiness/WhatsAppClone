package com.malakiapps.whatsappclone.android

import android.app.Application
import com.malakiapps.whatsappclone.common.di.initKoin
import org.koin.android.ext.koin.androidContext

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}