package com.malakiapps.whatsappclone.android.domain

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.malakiapps.whatsappclone.domain.messages.MessageNotification

class MessagesNotifications(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(messageNotification: MessageNotification){
        val intent = Intent(
            Intent.ACTION_VIEW,
            "fakeWhatsapp://conversation/${messageNotification.senderEmail.value}".toUri(),
            context,
            MainActivity::class.java
        )
        val pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat
            .Builder(context, MESSAGES_CHANNEL_ID)
            //.setSmallIcon()
            //.setContentTitle()
            //.setContentText()
            //.setStyle(Notification.)
            //.setContentIntent(pendingIntent)
            //.build()

        /*notificationManager.notify(
            "${messageNotification.senderEmail.value}_${messageNotification.messageId.value}".hashCode(),
            notification
        )*/

    }

    companion object {
        const val MESSAGES_CHANNEL_ID = "messages_channel"
    }
}