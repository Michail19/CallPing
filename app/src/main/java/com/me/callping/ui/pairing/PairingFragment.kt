package com.me.callping.ui.pairing

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.me.callping.R
import com.me.callping.core.pairing.PairedDevice
import com.me.callping.core.pairing.PairingViewModel
import com.me.callping.tools.QrBitmapGenerator
import com.me.callping.tools.QrPayloadBuilder
import org.json.JSONObject
import androidx.fragment.app.viewModels

class PairingFragment : Fragment(R.layout.fragment_pairing) {

    private val viewModel: PairingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupQrCode(view)
        setupScanButton(view)
    }

    private fun setupQrCode(view: View) {
        val qrText = QrPayloadBuilder.build()
        val qrBitmap = QrBitmapGenerator.generate(qrText)

        view.findViewById<ImageView>(R.id.qrImage).setImageBitmap(qrBitmap)
    }

    private fun setupScanButton(view: View) {
        view.findViewById<Button>(R.id.scanQrButton).setOnClickListener {
            scanLauncher.launch(ScanOptions())
        }
    }

    private val scanLauncher = registerForActivityResult(ScanContract()) {
        result -> result.contents?.let {
            handleOrResult(it)
        }
    }

    private fun handleOrResult(text: String) {
        val json = JSONObject(text)

        val device = PairedDevice(
            id = json.getString("serviceUuid"),
            name = json.getString("deviceName")
        )

        viewModel.addDevice(device)
    }
}