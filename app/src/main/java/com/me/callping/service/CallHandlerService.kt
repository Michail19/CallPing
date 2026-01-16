package com.me.callping.service

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi
import com.me.callping.core.call.CallEventDispatcher
import com.me.callping.core.transport.BleTransport
import com.me.callping.core.transport.TransportManager

class CallHandlerService : CallScreeningService() {

    override fun onCreate() {
        super.onCreate()

        TransportManager.registerTransport(
            BleTransport(applicationContext)
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onScreenCall(callDetails: Call.Details) {

        // Checking for incoming call
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) {
            return
        }

        // Logging call
        Log.d(TAG, "Incoming call detected")

        // Call processing
        CallEventDispatcher.handleIncomingCall()
    }

    override fun onDestroy() {
        TransportManager.unregisterAll()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "CallHandlerService"
    }
}