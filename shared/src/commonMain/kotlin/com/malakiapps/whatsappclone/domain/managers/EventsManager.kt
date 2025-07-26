package com.malakiapps.whatsappclone.domain.managers

import com.malakiapps.whatsappclone.domain.common.Event
import kotlinx.coroutines.channels.Channel

class EventsManager {
    val events = Channel<Event>()

    suspend fun sendEvent(event: Event){
        events.send(event)
    }
}