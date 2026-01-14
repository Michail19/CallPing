package com.me.callping.ui.devices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.me.callping.R
import com.me.callping.core.pairing.PairedDevice

class DeviceAdapter(
    private val onRemoveClick: (PairedDevice) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    private val items = mutableListOf<PairedDevice>()

    fun submitList(devices: List<PairedDevice>) {
        items.clear()
        items.addAll(devices)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.deviceName)
        private val remove = view.findViewById<ImageButton>(R.id.removeButton)

        fun bind(device: PairedDevice) {
            name.text = device.name
            remove.setOnClickListener {
                onRemoveClick(device)
            }
        }
    }
}
