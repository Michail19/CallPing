package com.me.callping.tools

import android.os.Build
import android.provider.Settings
import com.me.callping.core.App
import com.me.callping.data.CryptoManager
import org.json.JSONObject

object QrPayloadBuilder {

    private const val PROTOCOL_VERSION = 1

    fun build(): String {
        val deviceId = Settings.Secure.getString(
            App.appContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val json = JSONObject().apply {
            put("v", PROTOCOL_VERSION)
            put("deviceName", Build.MODEL)
            put("deviceId", deviceId)
            put("ts", System.currentTimeMillis())
        }.toString()

        return CryptoManager.encrypt(json)
    }
}
