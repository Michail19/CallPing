package com.me.callping.sender

import android.util.Log
import com.me.callping.model.CallEvent
import com.me.callping.model.CallEventType

object CallEventDispatcher {

    fun handleIncomingCall() {
        // Variable for storing a custom type of CallEvent
        val event = CallEvent(
            type = CallEventType.INCOMING_CALL,
            timestamp = System.currentTimeMillis()
        )

        Log.d(TAG, "Dispatching incoming call event")

        // Send message
        TransportManager.send(event)
    }

    private const val TAG = "CallHandlerService"
}