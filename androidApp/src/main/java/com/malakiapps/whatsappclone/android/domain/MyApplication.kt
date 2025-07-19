package com.malakiapps.whatsappclone.android.domain

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.malakiapps.whatsappclone.domain.di.initKoin
import org.koin.android.ext.koin.androidContext

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(config = {
            androidContext(this@MyApplication)
        })
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                MessagesNotifications.MESSAGES_CHANNEL_ID,
                "Messages",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Used to show messages sent by other users on the app"

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}