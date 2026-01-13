package com.me.callping.model

data class PairingPayload(
    val deviceName: String,
    val serviceUuid: String,
    val protocolVersion: Int
)
