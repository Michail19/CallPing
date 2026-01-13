package com.me.callping.pages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.me.callping.model.PairedDevice

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