package com.me.callping.core.call

data class CallEvent (
    val type: CallEventType,
    val timestamp: Long
) {
    fun toPayload(): ByteArray {
        return when (type) {
            CallEventType.INCOMING_CALL ->
                byteArrayOf(0x01)
        }
    }

    companion object {
        fun fromPayload(data: ByteArray): CallEvent? {
            return when (data.firstOrNull()) {
                0x01.toByte() ->
                    CallEvent(
                        type = CallEventType.INCOMING_CALL,
                        timestamp = System.currentTimeMillis()
                    )
                else -> null
            }
        }
    }
}
