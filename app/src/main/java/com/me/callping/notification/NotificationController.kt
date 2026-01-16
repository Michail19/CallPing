package com.me.callping.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.me.callping.R

object NotificationController {

    fun createServiceNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Call Listener")
            .setContentText("Listening for incoming calls")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
    }

    fun showIncomingCall(context: Context) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Incoming call")
            .setContentText("Call received on paired device")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(INCOMING_CALL_ID, notification)
    }

    private const val CHANNEL_ID = "call_listener"
    private const val INCOMING_CALL_ID = 2001
}