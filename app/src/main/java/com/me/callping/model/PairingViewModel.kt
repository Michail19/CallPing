package com.me.callping.model

import androidx.lifecycle.ViewModel

class PairingViewModel : ViewModel() {

    val pairedDevices = mutableListOf<PairedDevice>()

    fun addDevice(device: PairedDevice) {
        if (pairedDevices.none { it.id == device.id }) {
            pairedDevices.add(device)
        }
    }
}