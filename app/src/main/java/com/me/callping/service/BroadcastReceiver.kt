package com.me.callping.service

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BluetoothStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)

        when (state) {
            BluetoothAdapter.STATE_ON -> {
                val serviceIntent = Intent(context, ListenerService::class.java)
                    .putExtra("ble_restart", true)

                ContextCompat.startForegroundService(context, serviceIntent)
            }

            BluetoothAdapter.STATE_OFF -> {
                val serviceIntent = Intent(context, ListenerService::class.java)
                    .putExtra("ble_stop", true)

                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}
