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
        // 1. Сразу переводим сервис в Foreground режим (обязательно для Android 10+)
        startForeground(1, createNotification())

        val number = intent?.getStringExtra("EXTRA_NUMBER")
        Log.d(TAG, "Processing incoming call from: $number")

        // 2. Выполняем вашу логику отправки через BLE
        CallEventDispatcher.handleIncomingCall()

        // 3. Если задача разово отправить сигнал, можно остановить сервис после отправки.
        // Если логика отправки асинхронная, вызовите stopSelf() внутри callback-а успешной отправки.
        // stopSelf()

        return START_NOT_STICKY
    }

    private fun createNotification(): Notification {
        val channelId = "call_ping_channel"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Call Notifications", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Call Ping Active")
            .setContentText("Обработка входящего вызова...")
            .setSmallIcon(R.drawable.ic_launcher_background) // Замените на вашу иконку
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        TransportManager.unregisterAll()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "CallHandlerService"
    }
}
