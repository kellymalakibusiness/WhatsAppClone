package com.malakiapps.whatsappclone.android.domain.utils

import android.net.Uri

fun Uri.isUriAUrl(): Boolean {
    return this.toString().contains("http")
}