package com.me.callping.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.me.callping.R

object NotificationController {

    const val SERVICE = "service"
    const val INCOMING_CALL = "incoming_call"
    private const val INCOMING_CALL_ID = 2001

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

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(channel)
    }

    fun createServiceNotification(context: Context): Notification {
        ensureChannel(context, SERVICE, "Фоновая работа", NotificationManager.IMPORTANCE_MIN)

        return NotificationCompat.Builder(context, SERVICE)
            .setSmallIcon(R.drawable.launcher_foreground_standart)
            .setContentTitle("CallPing работает")
            .setContentText("Ожидание событий")
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    fun showIncomingCall(context: Context) {
        ensureChannel(context, INCOMING_CALL, "Входящие звонки", NotificationManager.IMPORTANCE_HIGH)

        val notification = NotificationCompat.Builder(context, INCOMING_CALL)
            .setContentTitle("Входящий звонок")
            .setContentText("Идёт вызов на сопряженное устройство") // .setContentText("Call received on paired device")
            .setSmallIcon(R.drawable.launcher_foreground_standart)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(INCOMING_CALL_ID, notification)
    }
}
