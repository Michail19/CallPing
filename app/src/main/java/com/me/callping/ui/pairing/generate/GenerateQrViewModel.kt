package com.me.callping.ui.pairing.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.callping.tools.QrBitmapGenerator
import com.me.callping.tools.QrPayloadBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GenerateQrViewModel(
    private val payloadBuilder: QrPayloadBuilder, // = QrPayloadBuilder(),
    private val qrGenerator: QrBitmapGenerator // = QrBitmapGenerator()
) : ViewModel() {

    private val _state = MutableStateFlow<GenerateQrState>(GenerateQrState.Loading)
    val state: StateFlow<GenerateQrState> = _state

    init {
        generateQR()
    }

    private fun generateQR() {
        viewModelScope.launch {
            try {
                val payload = payloadBuilder.build()
                val bitmap = qrGenerator.generate(payload)

                _state.value = GenerateQrState.Ready(bitmap)
            } catch (e: Exception) {
                _state.value = GenerateQrState.Error(e.message ?: "QR generation failed")
            }
        }
    }
}