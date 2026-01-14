package com.me.callping.ui.pairing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.me.callping.core.pairing.PairedDevice

class PairingViewModel : ViewModel() {
    
    private val _pairedDevice = MutableLiveData<List<PairedDevice>>(emptyList())
    val pairedDevice: LiveData<List<PairedDevice>> = _pairedDevice

    fun addDevice(device: PairedDevice) {
        val updated = _pairedDevice.value.orEmpty().toMutableList()

        if (updated.none { it.id == device.id }) {
            updated.add(device)
            _pairedDevice.value = updated
        }
    }
}