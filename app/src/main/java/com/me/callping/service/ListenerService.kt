package com.me.callping.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
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

        Log.d("BLE", "Scanner started")

        bleServer.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.getBooleanExtra("refresh_notification", false) == true) {
            Log.d("ListenerService", "Refreshing foreground notification")

            startForeground(
                NOTIFICATION_ID,
                NotificationController.createServiceNotification(this)
            )

            return START_STICKY
        }

        when {
            intent?.getBooleanExtra("ble_restart", false) == true -> {
                Log.d("ListenerService", "BLE restart requested")

                bleServer.stop()
                bleServer.start()
            }

            intent?.getBooleanExtra("ble_stop", false) == true -> {
                Log.d("ListenerService", "BLE stop requested")

                bleServer.stop()
            }
        }

        return START_STICKY
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