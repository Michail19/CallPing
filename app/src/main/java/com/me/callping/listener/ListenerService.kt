package com.me.callping.listener

import android.app.Service
import android.content.Intent
import android.os.IBinder


class ListenerService : Service() {

    private lateinit var bleServer: BleServer

    override fun onCreate() {
        super.onCreate()

        startForeground(
            NOTIFICATION_ID,
            NotificationController.createServiceNotification(this)
        )

        bleServer = BleServer(
            context = applicationContext,
            eventHandler = IncomingEventHandler(applicationContext)
        )

        bleServer.start()
    }

    override fun onDestroy() {
        bleServer.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
