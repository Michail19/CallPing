package com.me.callping.sender

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.util.Log
import com.me.callping.model.CallEvent
import java.util.UUID

class BleTransport (
    private val context: Context
    ) : Transport {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    private var advertiser: BluetoothLeAdvertiser? = null

    override fun start() {
        if (!isAvailable()) {
            Log.w(TAG, "BLE not available")
            return
        }

        advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        Log.d(TAG, "BLE advertiser started")
    }

    override fun isAvailable(): Boolean {
        return bluetoothAdapter != null &&
                bluetoothAdapter!!.isEnabled &&
                bluetoothAdapter!!.isMultipleAdvertisementSupported
    }

    override fun send(event: CallEvent) {
        val advertiser = advertiser ?: return

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
            .setConnectable(false)
            .build()

        val data = AdvertiseData.Builder()
            .addServiceUuid(SERVICE_UUID)
            .addManufacturerData(
                MANUFACTURER_ID,
                encodeEvent(event)
            )
            .setIncludeDeviceName(false)
            .build()

        advertiser.startAdvertising(settings, data, advertiseCallback)

        Log.d(TAG, "Advertising CallEvent: $event")
    }

    override fun stop() {
        advertiser?.stopAdvertising(advertiseCallback)
        advertiser = null
        Log.d(TAG, "BLE advertise stopped")
    }

    companion object {
        private const val TAG = "BLETransport"
    }
}