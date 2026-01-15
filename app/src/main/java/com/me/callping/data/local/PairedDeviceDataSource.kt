package com.me.callping.data.local

import android.content.Context
import com.me.callping.core.pairing.PairedDevice
import org.json.JSONArray
import org.json.JSONObject

class PairedDeviceDataSource(context: Context) {

    private val prefs = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun getAll(): List<PairedDevice> {
        val json = prefs.getString(KEY_DEVICES, null) ?: return emptyList()
        val array = JSONArray(json)

        return buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                add(
                    PairedDevice(
                        id = obj.getString("id"),
                        name = obj.getString("name")
                    )
                )
            }
        }
    }

    fun save(device: PairedDevice) {
        val devices = getAll().toMutableList()

        if (devices.any { it.id == device.id }) return

        devices.add(device)
        saveAll(devices)
    }

    fun remove(deviceId: String) {
        val updated = getAll().filterNot { it.id == deviceId }
        saveAll(updated)
    }

    fun clear() {
        prefs.edit().remove(KEY_DEVICES).apply()
    }

    private fun saveAll(devices: List<PairedDevice>) {
        val array = JSONArray().apply {
            devices.forEach { device ->
                put(
                    JSONObject().apply {
                        put("id", device.id)
                        put("name", device.name)
                    }
                )
            }
        }

        prefs.edit().putString(KEY_DEVICES, array.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "paired_device"
        private const val KEY_DEVICES = "devices"
    }
}
