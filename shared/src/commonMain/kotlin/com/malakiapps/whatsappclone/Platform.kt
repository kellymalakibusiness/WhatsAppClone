package com.malakiapps.whatsappclone

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform