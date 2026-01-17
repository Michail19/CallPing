package com.me.callping.tools

import android.os.Build
import android.provider.Settings
import com.me.callping.core.App
import org.json.JSONObject

object QrPayloadBuilder {

    private const val PROTOCOL_VERSION = 1

    fun build(): String {
        val deviceId = Settings.Secure.getString(
            App.appContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        return JSONObject().apply {
            put("deviceName", Build.MODEL)
            put("deviceId", deviceId)
            put("protocolVersion", PROTOCOL_VERSION)
        }.toString()
    }
}
