package com.me.callping.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.me.callping.R

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        view.findViewById<Button>(R.id.scanQrButton)
//            .setOnClickListener {
//                findNavController()
//                    .navigate(R.id.action_home_to_scanQr)
//            }

        view.findViewById<Button>(R.id.devicesButton)
            .setOnClickListener {
                findNavController()
                    .navigate(R.id.action_home_to_devices)
            }

        view.findViewById<Button>(R.id.showQrButton)
            .setOnClickListener {
                findNavController()
                    .navigate(R.id.action_home_to_generateQr)
            }
    }
}
