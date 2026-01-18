package com.me.callping.data

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoManager {

    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256
    private const val IV_SIZE = 12

    private val secretKey by lazy {
        val keyBytes = "CALLPING_SHARED_SECRET_32_BYTES!".toByteArray()
        SecretKeySpec(keyBytes, "AES")
    }

    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = ByteArray(IV_SIZE).also { SecureRandom().nextBytes(it) }

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        val encrypted = cipher.doFinal(plainText.toByteArray())

        return Base64.encodeToString(iv + encrypted, Base64.NO_WRAP)
    }

    fun decrypt(payload: String): String {
        val decoded = Base64.decode(payload, Base64.NO_WRAP)
        val iv = decoded.copyOfRange(0, IV_SIZE)
        val data = decoded.copyOfRange(IV_SIZE, decoded.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

        return String(cipher.doFinal(data))
    }
}
