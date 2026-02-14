package com.me.callping.tools

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.me.callping.notification.NotificationController
import com.me.callping.service.ListenerService

class KeepAliveWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        Log.d(TAG, "KeepAlive heartbeat")

        // 1. Обновляем уведомление (очень важно для MIUI)
        val notification = NotificationController.createServiceNotification(applicationContext)

        applicationContext.startForegroundService(
            Intent(applicationContext, ListenerService::class.java)
                .putExtra("refresh_notification", true)
        )

        Log.d(TAG, "Foreground notification refreshed")

        return Result.success()
    }

    companion object {
        private const val TAG = "KeepAliveWorker"
    }
}
