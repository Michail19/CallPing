package com.me.callping.core.transport

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.me.callping.core.BleConstants
import com.me.callping.core.call.CallEvent
import com.me.callping.core.call.CallEventDispatcher

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
        val adapter = bluetoothAdapter
        if (adapter == null) {
            Log.e(TAG, "Bluetooth adapter is NULL")
            return false
        }

        if (!adapter.isEnabled) {
            Log.e(TAG, "Bluetooth is DISABLED")
            return false
        }

        if (!adapter.isMultipleAdvertisementSupported) {
            Log.e(TAG, "BLE advertising NOT supported on this device")
            return false
        }

        return true
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    override fun send(event: CallEvent) {
        Log.d(TAG, "send() called")

        val advertiser = advertiser

        if (advertiser == null) {
            Log.e(TAG, "BLE advertiser is NULL — advertising not supported")
            return
        }

//        advertiser.stopAdvertising(advertiseCallback)

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .build()

        val data = AdvertiseData.Builder()
            .addServiceUuid(BleConstants.SERVICE_UUID)
            .addManufacturerData(
                BleConstants.MANUFACTURER_ID,
                event.toPayload()
            )
            .setIncludeDeviceName(false)
            .build()

        advertiser.startAdvertising(settings, data, advertiseCallback)

        Handler(Looper.getMainLooper()).postDelayed({
            advertiser.stopAdvertising(advertiseCallback)
            CallEventDispatcher.clearPending()
        }, 5_000)

        Log.d(TAG, "Advertising CallEvent: $event")
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    override fun stop() {
        advertiser?.stopAdvertising(advertiseCallback)
        advertiser = null
        Log.d(TAG, "BLE advertise stopped")
    }

    // --- Helpers ---

    private fun encodeEvent(event: CallEvent): ByteArray {
        return byteArrayOf(BleConstants.EVENT_INCOMING_CALL)
//        return byteArrayOf(
//            when (event.type.name) {
//                "INCOMING_CALL" -> 0x01
//                else -> 0x00
//            }
//        )
    }

    private val advertiseCallback = object : AdvertiseCallback() {

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.d(TAG, "BLE advertising started")
//            CallEventDispatcher.retryIfPending()
        }

        override fun onStartFailure(errorCode: Int) {
            Log.e(TAG, "BLE advertising failed: $errorCode")
        }
    }


    companion object {
        private const val TAG = "BLETransport"
    }
}