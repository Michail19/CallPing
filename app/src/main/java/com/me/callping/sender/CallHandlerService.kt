package com.me.callping.sender

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log

class CallHandlerService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {

        // Проверка на входящий звонок
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val direction = call.getDetails().getCallDirection();
            if (direction == Call.Details.DIRECTION_INCOMING) {
                return
            }
        }
        else {
            if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) {
                return
            }
        }
    }

    companion object {
        private const val TAG = "CallHandlerService"
    }
}