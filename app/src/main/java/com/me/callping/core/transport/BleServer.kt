package com.me.callping.core.transport

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.me.callping.core.BleConstants
import com.me.callping.core.call.CallEvent
import com.me.callping.core.call.IncomingEventHandler

class BleServer(
    private val context: Context,
    private val eventHandler: IncomingEventHandler
) {

    private val scanner by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter.bluetoothLeScanner
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isScanning = false
    private var scanPeriodRunnable: Runnable? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun start() {
        if (isScanning) {
            Log.w(TAG, "Already scanning")
            return
        }

        try {
            val filter = ScanFilter.Builder()
                .setServiceUuid(BleConstants.SERVICE_UUID)
                .build()

            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0) // Немедленные результаты
                .build()

            scanner.startScan(listOf(filter), settings, scanCallback)
            isScanning = true
            Log.d(TAG, "BLE scanning started")

            // Останавливаем сканирование через период
            scheduleScanStop()

        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: ${e.message}")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "IllegalStateException: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Exception starting scan: ${e.message}")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stop() {
        if (isScanning) {
            try {
                scanner.stopScan(scanCallback)
                Log.d(TAG, "BLE scanning stopped")
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping scan: ${e.message}")
            }
            isScanning = false
        }

        scanPeriodRunnable?.let {
            handler.removeCallbacks(it)
            scanPeriodRunnable = null
        }
    }

    private fun scheduleScanStop() {
        scanPeriodRunnable?.let {
            handler.removeCallbacks(it)
        }

        scanPeriodRunnable = Runnable {
            Log.d(TAG, "Scan period ended, restarting...")
            stop()
            // Перезапускаем сканирование после паузы
            handler.postDelayed({
                start()
            }, 2000)
        }

        handler.postDelayed(scanPeriodRunnable!!, BleConstants.SCAN_PERIOD_MS)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let { scanResult ->
                val manufacturerData = scanResult.scanRecord
                    ?.manufacturerSpecificData
                    ?.get(BleConstants.MANUFACTURER_ID)

                if (manufacturerData != null) {
                    Log.d(TAG, "Received BLE advertisement from ${scanResult.device?.address}")
                    Log.d(TAG, "RSSI: ${scanResult.rssi}, Data size: ${manufacturerData.size}")

                    val event = CallEvent.fromPayload(manufacturerData)
                    if (event != null) {
                        Log.d(TAG, "Decoded event: $event")
                        eventHandler.handle(event)
                    } else {
                        Log.w(TAG, "Failed to decode event from payload")
                    }
                }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach { result ->
                onScanResult(0, result)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "BLE scan failed: $errorCode")
            isScanning = false

            // Автоматический retry
            handler.postDelayed({
                start()
            }, 5000)
        }
    }

    companion object {
        private const val TAG = "BleServer"
    }
}