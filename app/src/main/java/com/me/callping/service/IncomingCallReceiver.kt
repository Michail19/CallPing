package com.me.callping.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

class IncomingCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Проверка на null и корректность экшена
        if (intent == null || intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        // Получаем состояние звонка
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        // Проверяем, что это именно начало звонка (RINGING)
        if (state == TelephonyManager.EXTRA_STATE_RINGING) {

            // Формируем Intent для запуска вашего ListenerService
            val serviceIntent = Intent(context, CallHandlerService::class.java).apply {
                // Передаем флаг события
                putExtra("CALL_EVENT", "INCOMING")
                // Можно добавить флаг для идентификации, что запуск пришел именно из ресивера
                action = "ACTION_START_BLE_ADVERTISING"
            }

            try {
                // В Android 10+ запуск Foreground сервиса из фона разрешен
                // во время активного входящего звонка
                ContextCompat.startForegroundService(context, serviceIntent)
            } catch (e: Exception) {
                // Логируем ошибку, если система все же заблокировала запуск
                e.printStackTrace()
            }
        }
    }
}
