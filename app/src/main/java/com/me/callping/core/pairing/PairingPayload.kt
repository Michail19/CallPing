package com.me.callping.core.pairing

data class PairingPayload(
    val deviceName: String,
    val serviceUuid: String,
    val protocolVersion: Int
)