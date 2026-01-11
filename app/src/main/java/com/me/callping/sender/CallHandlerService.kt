package com.me.callping.sender

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi

class CallHandlerService : CallScreeningService() {

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

    companion object {
        private const val TAG = "CallHandlerService"
    }
}