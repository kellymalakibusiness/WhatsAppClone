package com.malakiapps.whatsappclone.android.domain.utils

import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

suspend fun generateBase64ImageFromUrlUri(uri: Uri): String = withContext(Dispatchers.IO) {
    val connection = URL(uri.toString()).openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        connection.inputStream.use { it.readBytes() }.let {
            Base64.encodeToString(it, Base64.NO_WRAP)
        }
}