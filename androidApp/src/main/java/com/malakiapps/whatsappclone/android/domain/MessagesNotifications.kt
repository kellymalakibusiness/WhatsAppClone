package com.malakiapps.whatsappclone.android.domain

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.malakiapps.whatsappclone.android.R
import com.malakiapps.whatsappclone.domain.messages.MessageNotification
import com.malakiapps.whatsappclone.domain.user.Image
import kotlin.time.ExperimentalTime

class MessagesNotifications(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @OptIn(ExperimentalTime::class)
    fun showNotification(messageNotification: MessageNotification){
        val intent = Intent(
            Intent.ACTION_VIEW,
            "fakeWhatsapp://conversation/${messageNotification.senderEmail.value}".toUri(),
            context,
            MainActivity::class.java
        )

        val senderImage = messageNotification.targetImage?.base64ImageToBitmap()

        val pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat
            .Builder(context, MESSAGES_CHANNEL_ID)
            .setSmallIcon(R.drawable.chat)
            .setLargeIcon(senderImage)
            .setContentTitle(messageNotification.name.value)
            .setContentText(messageNotification.message.value)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.chat, "REPLY", pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            "${messageNotification.senderEmail.value}_${messageNotification.messageId.value}".hashCode(),
            notification
        )
    }

    private fun Image.base64ImageToBitmap(): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(value, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val MESSAGES_CHANNEL_ID = "messages_channel"
    }
}