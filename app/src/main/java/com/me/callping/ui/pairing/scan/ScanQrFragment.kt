package com.me.callping.ui.pairing.scan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.me.callping.R
import com.me.callping.core.pairing.PairingViewModel
import com.me.callping.data.local.PairedDeviceDataSource
import com.me.callping.data.repository.PairedDeviceRepository
import kotlinx.coroutines.launch

class ScanQrFragment : Fragment(R.layout.fragment_device_list){

    private val viewModel: ScanQrViewModel by viewModels()
//    private val pairingSharedViewModel: PairingViewModel by viewModels()

    private val repository by lazy {
        PairedDeviceRepository(
            PairedDeviceDataSource(requireContext())
        )
    }

    private val scanLauncher = registerForActivityResult(ScanContract()) {
        result -> if (result.contents != null) viewModel.onQrScanned(result.contents)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        view.findViewById<Button>(R.id.scanQrButton).setOnClickListener {
//            startScan()
//        }

        startScan()
        observeState()
    }

    private fun startScan() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Отсканируйте QR")
            setBeepEnabled(true)
            setOrientationLocked(false)
        }

        scanLauncher.launch(options)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when(state) {
                    is ScanQrState.Idle -> Unit

                    is ScanQrState.Scanning -> {
                        // можно показать progress
                    }

                    is ScanQrState.Success -> {
//                        pairingSharedViewModel.addDevice(state.device)
                        repository.addDevice(state.device)
                        findNavController().popBackStack()
                    }

                    is ScanQrState.Error -> {
                        viewModel.reset()
                    }
                }
            }
        }
    }
}
