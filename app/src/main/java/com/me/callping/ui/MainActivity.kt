package com.me.callping.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.me.callping.R
import com.me.callping.service.ListenerService

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSIONS = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }) {
            startListenerService()
        } else {
            requestPermissions(permissions, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            startListenerService()
        }
    }

    private fun startListenerService() {
        val intent = Intent(this, ListenerService::class.java)
        startForegroundService(intent)
    }
}
