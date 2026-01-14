package com.me.callping.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.me.callping.core.call.IncomingEventHandler
import com.me.callping.core.transport.BleServer
import com.me.callping.notification.NotificationController

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