package com.me.callping.ui.devices

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.me.callping.R
import com.me.callping.data.local.PairedDeviceDataSource
import com.me.callping.data.repository.PairedDeviceRepository
import kotlinx.coroutines.launch

class DeviceListFragment : Fragment(R.layout.fragment_device_list) {

    private val viewModel: DeviceListViewModel by viewModels {
        val repo = PairedDeviceRepository(
            PairedDeviceDataSource(requireContext())
        )
        DeviceListViewModelFactory(repo)
    }

    private lateinit var adapter: DeviceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        val emptyText = view.findViewById<TextView>(R.id.emptyText)

        adapter = DeviceAdapter {
            viewModel.removeDevice(it)
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                adapter.submitList(state.devices)
                emptyText.visibility =
                    if (state.isEmpty) View.VISIBLE else View.GONE
            }
        }

        view.findViewById<Button>(R.id.scanButton).setOnClickListener {
            findNavController().navigate(R.id.action_deviceListFragment_to_scanQrFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDevices()
    }
}
