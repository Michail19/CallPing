package com.me.callping.ui.pairing.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.me.callping.tools.QrBitmapGenerator
import com.me.callping.tools.QrPayloadBuilder

class GenerateQrViewModelFactory(
    private val payloadBuilder: QrPayloadBuilder,
    private val qrGenerator: QrBitmapGenerator
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GenerateQrViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GenerateQrViewModel(
                payloadBuilder,
                qrGenerator
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}