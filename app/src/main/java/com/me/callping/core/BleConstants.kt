package com.me.callping.core

import android.os.ParcelUuid

object BleConstants {

    val SERVICE_UUID: ParcelUuid = ParcelUuid.fromString(
        "0000b81d-0000-1000-8000-00805f9b34fb"
    ) // The service's UUID is the "this is our app" token

    const val MANUFACTURER_ID = 0xFFFF // Manufacturer ID — arbitrary (not conflicting)

    // Event types (payload)
    const val EVENT_INCOMING_CALL: Byte = 0x01
    const val EVENT_CALL_ENDED: Byte = 0x02

    // Advertisement timeout
    const val ADVERTISING_TIMEOUT_MS = 10000L // 10 секунд

    // Scan settings
    const val SCAN_PERIOD_MS = 10000L
}