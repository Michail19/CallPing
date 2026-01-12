package com.me.callping.listener

import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context

class BleServer(
    private val context: Context,
    private val eventHandler: IncomingEventHandler
) {

    private val scanner by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter.bluetoothLeScanner
    }

    fun start() {
        val filter = ScanFilter.Builder()
            .setServiceUuid(BleConstants.SERVICE_UUID)
            .build()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner.startScan(listOf(filter), settings, scanCallback)
    }

    fun stop() {
        scanner.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            val data = result.scanRecord?.manufacturerSpecificData
            ?.get(BleConstants.MANUFACTURER_ID)
            ?: return

            eventHandler.handle(data)
        }
    }
}
