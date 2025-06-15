package com.malakiapps.whatsappclone.data.common

import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

suspend fun Uri.generateBase64ImageFromUrlUri(): String = withContext(Dispatchers.IO) {
    val connection = URL(this@generateBase64ImageFromUrlUri.toString()).openConnection() as HttpURLConnection
    connection.doInput = true
    connection.connect()
    connection.inputStream.use { it.readBytes() }.let {
        Base64.encodeToString(it, Base64.NO_WRAP)
    }
}