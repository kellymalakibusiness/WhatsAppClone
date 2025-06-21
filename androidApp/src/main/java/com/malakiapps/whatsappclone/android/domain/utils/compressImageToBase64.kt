package com.malakiapps.whatsappclone.android.domain.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.malakiapps.whatsappclone.domain.user.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

suspend fun compressImageToBase64(image: Uri, contentResolver: ContentResolver): Image? = withContext(Dispatchers.IO){
    val maxSizeInKb = 60

    val stream = contentResolver.openInputStream(image) ?: return@withContext null
    val bitmap = BitmapFactory.decodeStream(stream)
    stream.close()

    var quality = 100
    var outputStream: ByteArrayOutputStream

    do {
        outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        quality -= 5
    } while (outputStream.size() > maxSizeInKb * 1024 && quality > 10)

    Image(Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP))
}