package com.me.callping.sender

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.util.Log
import com.me.callping.model.CallEvent

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
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "BLETransport"
    }
}