package com.me.callping.ui.devices

import com.me.callping.core.pairing.PairedDevice

data class DeviceListState (
    val devices: List<PairedDevice> = emptyList()
) {
    val isEmpty: Boolean get() = devices.isEmpty()
}
