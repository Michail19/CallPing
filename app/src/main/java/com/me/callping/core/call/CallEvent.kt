package com.me.callping.core.call

import com.me.callping.core.BleConstants
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class CallEvent(
    val type: CallEventType,
    val timestamp: Long
) {
    fun toPayload(): ByteArray {
        // Совместимый метод с BLETransport.encodeEvent()
        val buffer = ByteBuffer.allocate(9)
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        buffer.put(when (type) {
            CallEventType.INCOMING_CALL -> BleConstants.EVENT_INCOMING_CALL
        })

        buffer.putLong(timestamp)

        return buffer.array()
    }

    companion object {
        fun fromPayload(data: ByteArray): CallEvent? {
            if (data.size < 9) return null

            val buffer = ByteBuffer.wrap(data)
            buffer.order(ByteOrder.LITTLE_ENDIAN)

            val eventType = buffer.get()
            val timestamp = buffer.long

            return when (eventType.toInt()) {
                BleConstants.EVENT_INCOMING_CALL.toInt() ->
                    CallEvent(CallEventType.INCOMING_CALL, timestamp)
                else -> null
            }
        }
    }
}
