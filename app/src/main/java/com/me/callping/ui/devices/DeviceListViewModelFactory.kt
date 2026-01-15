package com.me.callping.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.me.callping.data.repository.PairedDeviceRepository

class DeviceListViewModelFactory(
    private val repository: PairedDeviceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DeviceListViewModel(repository) as T
    }
}
