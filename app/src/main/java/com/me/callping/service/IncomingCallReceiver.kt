package com.me.callping.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat

class IncomingCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Проверяем состояние звонка
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)

        Log.d("CALLPING", state.toString())

        // Проверяем, что это звонок
        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                val serviceIntent = Intent(context, CallHandlerService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
            }

            TelephonyManager.EXTRA_STATE_IDLE -> {
                // Звонок завершён → останавливаем сервис
                context.stopService(
                    Intent(context, CallHandlerService::class.java)
                )
            }
        }
    }
}
