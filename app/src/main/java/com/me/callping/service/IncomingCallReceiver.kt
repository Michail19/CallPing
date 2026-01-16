package com.me.callping.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

class IncomingCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
            val serviceIntent = Intent(context, ListenerService::class.java).apply {
                putExtra("CALL_EVENT", "INCOMING")
            }

            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
