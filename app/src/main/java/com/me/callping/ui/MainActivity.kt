package com.me.callping.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.me.callping.R
import com.me.callping.service.ListenerService
import com.me.callping.tools.KeepAliveWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSIONS = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val workRequest =
            PeriodicWorkRequestBuilder<KeepAliveWorker>(6, TimeUnit.HOURS)
                .addTag("keep_alive")
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "keep_alive_worker",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        // 1. BLE разрешения (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN)
            permissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissionsNeeded.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        } else {
            // Для Android 11 и ниже
            permissionsNeeded.add(Manifest.permission.BLUETOOTH)
            permissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN)
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // 2. Уведомления (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // 3. Работа со звонками и телефоном
        permissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionsNeeded.add(Manifest.permission.MANAGE_OWN_CALLS)
        }

        // 4. Камера
        permissionsNeeded.add(Manifest.permission.CAMERA)

        // Фильтруем те, что еще не даны
        val listToRequest = permissionsNeeded.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (listToRequest.isEmpty()) {
            startListenerService()
        } else {
            ActivityCompat.requestPermissions(this, listToRequest.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // Проверяем, даны ли обязательные разрешения для работы BLE
            val allGranted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (allGranted) {
                startListenerService()
            } else {
                // Можно добавить диалог, объясняющий, почему приложение не будет работать
            }
        }
    }

    private fun startListenerService() {
        val intent = Intent(this, ListenerService::class.java)
        // Для Foreground Service на Android 8.0+ используем ContextCompat
        ContextCompat.startForegroundService(this, intent)
    }
}
