package com.me.callping.data.repository

import com.me.callping.core.pairing.PairedDevice
import com.me.callping.data.local.PairedDeviceDataSource

class PairedDeviceRepository (
    private val dataSource: PairedDeviceDataSource
) {
    fun getDevices(): List<PairedDevice> = dataSource.getAll()
    fun addDevice(device: PairedDevice) = dataSource.save(device)
    fun removeDevice(id: String) = dataSource.remove(id)
}
