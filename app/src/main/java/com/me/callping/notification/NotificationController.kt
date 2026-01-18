package com.me.callping.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.me.callping.R

object NotificationController {

    const val SERVICE = "service"
    const val INCOMING_CALL = "incoming_call"
    private const val INCOMING_CALL_ID = 2001

    /**
     * Выбирает иконку в зависимости от версии системы.
     * Для Android 14 (API 34) и выше (включая 15 и 16) используем плоский силуэт.
     * Для старых версий пробуем оставить иконку приложения.
     */
    @DrawableRes
    private fun getSmallIconRes(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14, 15, 16+: строго монохромный векторный ресурс
            R.drawable.launcher_foreground_standart
        } else {
            // Старые версии: иконка приложения
            R.mipmap.ic_launcher_new
        }
    }

    private fun ensureChannel(context: Context, CHANNEL_ID: String, CHANNEL_NAME: String, importance: Int) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            importance
        ).apply {
            setSound(null, null)
            enableVibration(false)
            setShowBadge(false)
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun createServiceNotification(context: Context): Notification {
        ensureChannel(context, SERVICE, "Фоновая работа", NotificationManager.IMPORTANCE_MIN)

        return NotificationCompat.Builder(context, SERVICE)
            .setSmallIcon(getSmallIconRes())
            .setContentTitle("CallPing работает")
            .setContentText("Ожидание событий")
            .setOngoing(true)
            .setSilent(true)
            .setColor(ContextCompat.getColor(context, R.color.notification_accent))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    fun showIncomingCall(context: Context) {
        ensureChannel(context, INCOMING_CALL, "Входящие звонки", NotificationManager.IMPORTANCE_HIGH)

        val notification = NotificationCompat.Builder(context, INCOMING_CALL)
            .setContentTitle("Входящий звонок")
            .setContentText("Идёт вызов на сопряженное устройство")
            .setSmallIcon(getSmallIconRes())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setColor(ContextCompat.getColor(context, R.color.notification_accent))
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(INCOMING_CALL_ID, notification)
    }
}
