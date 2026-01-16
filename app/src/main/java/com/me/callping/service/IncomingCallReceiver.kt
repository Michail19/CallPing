package com.me.callping.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.me.callping.core.call.CallEventDispatcher

class IncomingCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Проверяем состояние звонка
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)

        Log.d("CALLPING", state.toString())

        // Проверяем, что это именно начало звонка (RINGING)
        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
            // Номер телефона (требует разрешения READ_CALL_LOG)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            // Запускаем наш сервис
            val serviceIntent = Intent(context, CallHandlerService::class.java).apply {
                putExtra("EXTRA_NUMBER", incomingNumber)
            }
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
