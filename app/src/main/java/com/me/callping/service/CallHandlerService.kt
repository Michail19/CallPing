package com.me.callping.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.me.callping.R
import com.me.callping.core.call.CallEventDispatcher
import com.me.callping.core.transport.BleTransport
import com.me.callping.core.transport.TransportManager

class CallHandlerService : Service() {

    override fun onCreate() {
        super.onCreate()
        // Регистрируем транспорт при создании сервиса
        TransportManager.registerTransport(BleTransport(applicationContext))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Сразу переводим сервис в Foreground режим (обязательно для Android 10+)
        startForeground(1, createNotification())

        val number = intent?.getStringExtra("EXTRA_NUMBER")
        Log.d(TAG, "Processing incoming call from: $number")

        // Выполняем вашу логику отправки через BLE
        CallEventDispatcher.handleIncomingCall()

        return START_NOT_STICKY
    }

    private fun createNotification(): Notification {
        val channel = NotificationChannel(CHANNEL_ID, "Уведомление об обработке вызова", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("CallPing активен")
            .setContentText("Обработка входящего вызова...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        getSystemService(NotificationManager::class.java).deleteNotificationChannel(CHANNEL_ID)
        TransportManager.unregisterAll()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "CallHandlerService"
        private const val CHANNEL_ID = "call_ping_channel"
    }
}
