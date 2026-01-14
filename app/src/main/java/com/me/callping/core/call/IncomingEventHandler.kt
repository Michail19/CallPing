package com.me.callping.core.call

import android.content.Context
import com.me.callping.core.BleConstants
import com.me.callping.notification.NotificationController

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
            BleConstants.EVENT_INCOMING_CALL -> CallEventType.INCOMING_CALL
            else -> return null
        }

        return CallEvent(
            type = type,
            timestamp = System.currentTimeMillis()
        )
    }

}