package com.me.callping.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.me.callping.R

object NotificationController {

    private const val CHANNEL_ID = "call_listener"
    private const val CHANNEL_NAME = "Call Listener"
    private const val INCOMING_CALL_ID = 2001

    private fun ensureChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Foreground service for listening incoming calls"
        }

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(channel)
    }

    fun createServiceNotification(context: Context): Notification {
        ensureChannel(context)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Call Listener")
            .setContentText("Listening for incoming calls")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()
    }

    fun showIncomingCall(context: Context) {
        ensureChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Incoming call")
            .setContentText("Call received on paired device")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(INCOMING_CALL_ID, notification)
    }
}
