package com.me.callping.sender

import com.me.callping.model.CallEvent

object TransportManager {

    private val transports = mutableListOf<Transport>()

    fun registerTransport(transport: Transport) {
        transports.add(transport)
        transport.start()
    }

    fun unregisterAll() {
        transports.forEach { it.stop() }
        transports.clear()
    }

    fun send(event: CallEvent) {
        val transport = selectAvailableTransport()
        transport?.send(event)
    }

    private fun selectAvailableTransport(): Transport? {
        return transports.firstOrNull { it.isAvailable() }
    }
}
