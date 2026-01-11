package com.me.callping.sender

import com.me.callping.model.CallEvent

interface Transport {
    fun start() // Initialization of transport
    fun isAvailable(): Boolean // Is possible to use transport
    fun send(event: CallEvent) // Sending event
    fun stop() // Stopping transport thread
}
