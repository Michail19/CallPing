package com.me.callping.ui.pairing.generate

import android.graphics.Bitmap

sealed class GenerateQrState {
    object Loading : GenerateQrState()
    data class Ready(val bitmap: Bitmap) : GenerateQrState()
    data class Error(val message: String) : GenerateQrState()
}
