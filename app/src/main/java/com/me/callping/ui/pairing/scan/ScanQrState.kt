package com.me.callping.ui.pairing.scan

import com.me.callping.core.pairing.PairedDevice

sealed class ScanQrState {
    object Idle : ScanQrState()
    object Scanning : ScanQrState()
    data class Success(val device: PairedDevice) : ScanQrState()
    data class Error(val message: String) : ScanQrState()
}
