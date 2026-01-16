package com.me.callping.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.telecom.TelecomManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.me.callping.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isCallScreeningApp(this)) {
            showCallScreeningDialog(this)
        }

        val telecom = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        Log.e(
            "CALL_PING",
            "isDefaultCallScreeningApp = ${telecom.defaultDialerPackage == packageName}"
        )

        if (telecom.defaultDialerPackage != packageName) {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            }
            startActivity(intent)
        }

        Log.e(
            "CALL_PING",
            "defaultDialer = ${telecom.defaultDialerPackage}"
        )
    }

    fun showCallScreeningDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Требуется доступ к звонкам")
            .setMessage(
                "Чтобы приложение могло уведомлять другие устройства о входящих звонках, " +
                        "назначьте CallPing определителем вызовов."
            )
            .setPositiveButton("Открыть настройки") { _, _ ->
                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                context.startActivity(intent)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    fun openCallScreeningSettings(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun isCallScreeningApp(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return false

        val telecomManager = context.getSystemService<TelecomManager>()
        return telecomManager?.defaultDialerPackage == context.packageName
    }
}
