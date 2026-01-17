package com.me.callping.tools

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object QrBitmapGenerator {

    fun generate(text: String): Bitmap {
        val matrix = QRCodeWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            512,
            512
        )

        return Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565).apply {
            for (x in 0 until 512) {
                for (y in 0 until 512) {
                    setPixel(
                        x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE
                    )
                }
            }
        }
    }
}
