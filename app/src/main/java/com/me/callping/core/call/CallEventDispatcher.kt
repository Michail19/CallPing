package com.me.callping.core.call

import android.util.Log
import com.me.callping.core.transport.TransportManager

object CallEventDispatcher {

    private var pendingEvent: CallEvent? = null

    fun handleIncomingCall() {
        // Variable for storing a custom type of CallEvent
        val event = CallEvent(
            type = CallEventType.INCOMING_CALL,
            timestamp = System.currentTimeMillis()
        )

        Log.d(TAG, "Dispatching incoming call event")

        // Send message
        pendingEvent = event
        TransportManager.send(event)
    }

    fun retryIfPending() {
        pendingEvent?.let {
            TransportManager.send(it)
        }
    }

    fun clearPending() {
        pendingEvent = null
    }

    private const val TAG = "CallHandlerService"
}