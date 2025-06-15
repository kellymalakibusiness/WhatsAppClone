package com.malakiapps.whatsappclone.android.domain

import android.app.Application
import com.malakiapps.whatsappclone.domain.di.initKoin
import org.koin.android.ext.koin.androidContext

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(config = {
            androidContext(this@MyApplication)
        })
    }
}