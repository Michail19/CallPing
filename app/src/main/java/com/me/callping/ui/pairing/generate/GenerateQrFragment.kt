package com.me.callping.ui.pairing.generate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.me.callping.R
import com.me.callping.tools.QrBitmapGenerator
import com.me.callping.tools.QrPayloadBuilder
import kotlinx.coroutines.launch

class GenerateQrFragment : Fragment(R.layout.fragment_generate_qr) {

    private val viewModel: GenerateQrViewModel by viewModels {
        GenerateQrViewModelFactory(
            payloadBuilder = QrPayloadBuilder,
            qrGenerator = QrBitmapGenerator
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrImage = view.findViewById<ImageView>(R.id.qrImage)
        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val errorText = view.findViewById<TextView>(R.id.errorText)
        val helpTextView = view.findViewById<TextView>(R.id.helpText)

        helpTextView.setOnClickListener {
            val url = "https://github.com/Michail19/CallPing/blob/main/README.md"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is GenerateQrState.Loading -> {
                        progress.visibility = View.VISIBLE
                        qrImage.visibility = View.GONE
                        errorText.visibility = View.GONE
                    }

                    is GenerateQrState.Ready -> {
                        progress.visibility = View.GONE
                        qrImage.visibility = View.VISIBLE
                        errorText.visibility = View.GONE
                        qrImage.setImageBitmap(state.bitmap)
                    }

                    is GenerateQrState.Error -> {
                        progress.visibility = View.GONE
                        qrImage.visibility = View.GONE
                        errorText.visibility = View.VISIBLE
                        errorText.text = state.message
                    }
                }
            }
        }
    }
}
