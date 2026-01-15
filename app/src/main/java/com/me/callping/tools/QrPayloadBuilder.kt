package com.me.callping.tools

import android.os.Build
import com.me.callping.core.BleConstants
import com.me.callping.core.pairing.PairingPayload
import org.json.JSONObject

object QrPayloadBuilder {

    fun build(): String {
        val payload = PairingPayload(
            deviceName = Build.MODEL,
            serviceUuid = BleConstants.SERVICE_UUID.toString(),
            protocolVersion = 1
        )

        return JSONObject().apply {
            put("deviceName", payload.deviceName)
            put("serviceUuid", payload.serviceUuid)
            put("protocolVersion", payload.protocolVersion)
        }.toString()
    }
}