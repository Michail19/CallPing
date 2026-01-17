package com.me.callping.core

import android.os.ParcelUuid

object BleConstants {

    val SERVICE_UUID: ParcelUuid = ParcelUuid.fromString(
        "12345678-1234-1234-1234-123456789abc"
    ) // The service's UUID is the "this is our app" token

    const val MANUFACTURER_ID = 0x1234 // Manufacturer ID — arbitrary (not conflicting)

    const val EVENT_INCOMING_CALL: Byte = 0x01 // Event types (payload)
}