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
import com.me.callping.core.call.CallEventType
import java.nio.ByteBuffer
import java.nio.ByteOrder

class BleTransport (
    private val context: Context
    ) : Transport {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    private var advertiser: BluetoothLeAdvertiser? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isAdvertising = false
    private var stopAdvertisingRunnable: Runnable? = null

    override fun start() {
        if (!isAvailable()) {
            Log.w(TAG, "BLE not available")
            return
        }

        advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
        Log.d(TAG, "BLE advertiser started")
    }

    override fun isAvailable(): Boolean {
        val adapterExists = bluetoothAdapter != null
        val enabled = bluetoothAdapter?.isEnabled == true
        val advSupported = bluetoothAdapter?.isMultipleAdvertisementSupported == true

        Log.d(TAG, "BLE check: adapter=$adapterExists enabled=$enabled advSupported=$advSupported")

        return adapterExists && enabled && advSupported

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    override fun send(event: CallEvent) {
        val advertiser = advertiser ?: return

        // Останавливаем предыдущую рекламу
        advertiser.stopAdvertising(advertiseCallback)

        // Создаем настройки рекламы
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .setTimeout(0)
            .build()

        // Создаем данные для рекламы
        val payload = encodeEvent(event)
        Log.d(TAG, "Payload size: ${payload.size} bytes")

        val data = AdvertiseData.Builder()
            .addServiceUuid(BleConstants.SERVICE_UUID)
            .addManufacturerData(
                BleConstants.MANUFACTURER_ID,
                payload
            )
            .setIncludeDeviceName(false)
            .setIncludeTxPowerLevel(true)
            .build()

        // Для некоторых устройств нужен scan response
        val scanResponse = AdvertiseData.Builder()
            .addServiceUuid(BleConstants.SERVICE_UUID)
            .build()

        try {
            advertiser.startAdvertising(settings, data, scanResponse, advertiseCallback)
            isAdvertising = true
            Log.d(TAG, "Advertising started for event: $event")

            // Планируем остановку через таймаут
            scheduleAdvertisingStop()

        } catch (e: IllegalStateException) {
            Log.e(TAG, "IllegalStateException: ${e.message}")
            isAdvertising = false
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: ${e.message}")
            isAdvertising = false
        } catch (e: Exception) {
            Log.e(TAG, "Exception during advertising: ${e.message}")
            isAdvertising = false
        }

        Log.d(TAG, "Advertising CallEvent: $event")
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    override fun stop() {
        stopCurrentAdvertising()
        Log.d(TAG, "BLE advertise stopped")
    }

    // --- Helpers ---

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    private fun stopCurrentAdvertising() {
        if (isAdvertising) {
            try {
                advertiser?.stopAdvertising(advertiseCallback)
                Log.d(TAG, "Advertising stopped")
            } catch (e: IllegalStateException) {
                Log.w(TAG, "Already stopped advertising")
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping advertising: ${e.message}")
            }
            isAdvertising = false
        }

        // Убираем pending runnable
        stopAdvertisingRunnable?.let {
            handler.removeCallbacks(it)
            stopAdvertisingRunnable = null
        }
    }

    private fun scheduleAdvertisingStop() {
        // Отменяем предыдущий таймер
        stopAdvertisingRunnable?.let {
            handler.removeCallbacks(it)
        }

        // Создаем новый таймер
        stopAdvertisingRunnable = Runnable {
            Log.d(TAG, "Advertising timeout reached, stopping...")
            stopCurrentAdvertising()
            CallEventDispatcher.clearPending()
        }

        // Запускаем таймер
        handler.postDelayed(stopAdvertisingRunnable!!, BleConstants.ADVERTISING_TIMEOUT_MS)
    }

    private fun encodeEvent(event: CallEvent): ByteArray {
        // Улучшенная кодировка: тип события + timestamp
        val buffer = ByteBuffer.allocate(9) // 1 байт тип + 8 байт timestamp
        buffer.order(ByteOrder.LITTLE_ENDIAN) // BLE использует little endian

        // Тип события
        buffer.put(when (event.type) {
            CallEventType.INCOMING_CALL -> BleConstants.EVENT_INCOMING_CALL
        })

        // Timestamp (8 байт)
        buffer.putLong(event.timestamp)

        return buffer.array()
    }

    private val advertiseCallback = object : AdvertiseCallback() {

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.d(TAG, "BLE advertising started")
            CallEventDispatcher.retryIfPending()
        }

        override fun onStartFailure(errorCode: Int) {
            Log.e(TAG, "BLE advertising failed: $errorCode")
        }
    }

    companion object {
        private const val TAG = "BLETransport"
    }
}