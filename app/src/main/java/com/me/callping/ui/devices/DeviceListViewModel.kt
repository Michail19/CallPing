package com.me.callping.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.callping.core.pairing.PairedDevice
import com.me.callping.data.repository.PairedDeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeviceListViewModel(
    private val repository: PairedDeviceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DeviceListState())
    val state: StateFlow<DeviceListState> = _state

    init {
        loadDevices()
    }

    fun loadDevices() {
        viewModelScope.launch {
            val devices = repository.getDevices()
            _state.value = DeviceListState(devices)
        }
    }

    fun removeDevice(device: PairedDevice) {
        viewModelScope.launch {
            repository.removeDevice(device.id)
            loadDevices()
        }
    }
}
