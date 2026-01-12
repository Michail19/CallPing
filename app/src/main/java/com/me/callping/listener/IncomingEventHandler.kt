package com.me.callping.listener

import android.content.Context
import com.me.callping.model.CallEvent
import com.me.callping.model.CallEventType

class IncomingEventHandler(
    private val context: Context
) {

    fun handle(payload: ByteArray) {
        val event = decode(payload) ?: return

        when (event.type) {
            CallEventType.INCOMING_CALL -> NotificationController.showIncomingCall(context)
        }
    }

    private fun decode(payload: ByteArray): CallEvent? {
        if (payload.isEmpty()) return null

        val type = when (payload[0]) {
            0x01.toByte() -> CallEventType.INCOMING_CALL
            else -> return null
        }

        return CallEvent(
            type = type,
            timestamp = System.currentTimeMillis()
        )
    }

}