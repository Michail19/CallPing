package com.me.callping.core.pairing

data class PairingPayload(
    val deviceName: String,
    val deviceId: String,
    val protocolVersion: Int
)
