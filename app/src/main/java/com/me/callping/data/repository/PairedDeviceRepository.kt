package com.me.callping.data.repository

import com.me.callping.core.pairing.PairedDevice
import kotlinx.coroutines.flow.Flow

interface PairedDeviceRepository {
    fun observeDevice(): Flow<List<PairedDevice>>
    suspend fun addDevice(device: PairedDevice)
    suspend fun removeDevice(id: String)
}