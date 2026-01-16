package com.me.callping.core.transport

import android.util.Log
import com.me.callping.core.call.CallEvent

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

        Log.d("TransportManager", "Transport EVENT_INCOMING_CALL")
    }

    private fun selectAvailableTransport(): Transport? {
        return transports.firstOrNull { it.isAvailable() }
    }
}
