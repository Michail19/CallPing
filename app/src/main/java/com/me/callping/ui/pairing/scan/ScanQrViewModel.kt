package com.me.callping.ui.pairing.scan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.callping.core.pairing.PairedDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class ScanQrViewModel : ViewModel() {

    private val _state = MutableStateFlow<ScanQrState>(ScanQrState.Scanning)
    val state: StateFlow<ScanQrState> = _state

    fun onQrScanned(text: String) {
        viewModelScope.launch {
            try {
                _state.value = ScanQrState.Scanning

                val json = JSONObject(text)

                val device = PairedDevice(
                    id = json.getString("deviceId"),
                    name = json.getString("deviceName")
                )

                _state.value = ScanQrState.Success(device)

                Log.d("onQrScanned", "Scanned successful")
                Log.d("onQrScanned", device.id + " " + device.name)
            } catch (e: Exception) {
                _state.value = ScanQrState.Error(e.message ?: "Invalid QR code")
            }
        }
    }

    fun reset() {
        _state.value = ScanQrState.Idle
    }
}
