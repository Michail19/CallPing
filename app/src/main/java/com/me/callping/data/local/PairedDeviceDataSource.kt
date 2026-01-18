package com.me.callping.data.local

import android.content.Context
import android.util.Base64
import com.me.callping.core.pairing.PairedDevice
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyGenParameterSpec
import java.security.KeyStore
import kotlin.random.Random

class PairedDeviceDataSource(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // --- Keystore key ---
    private val keyAlias = "paired_device_key"
    private val key: SecretKey by lazy { getOrCreateKey() }

    // --- Encryption parameters ---
    private val charset = Charset.forName("UTF-8")
    private val GCM_TAG_LENGTH = 128

    fun getAll(): List<PairedDevice> {
        val json = prefs.getString(KEY_DEVICES, null) ?: return emptyList()
        val array = JSONArray(json)

        return buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val encryptedId = obj.getString("id")
                val iv = Base64.decode(obj.getString("iv"), Base64.DEFAULT)
                add(
                    PairedDevice(
                        id = decrypt(encryptedId, iv),
                        name = obj.getString("name")
                    )
                )
            }
        }
    }

    fun save(device: PairedDevice) {
        val devices = getAll().toMutableList()

        if (devices.any { it.id == device.id }) return

        devices.add(device)
        saveAll(devices)
    }

    fun remove(deviceId: String) {
        val updated = getAll().filterNot { it.id == deviceId }
        saveAll(updated)
    }

    fun clear() {
        prefs.edit().remove(KEY_DEVICES).apply()
    }

    private fun saveAll(devices: List<PairedDevice>) {
        val array = JSONArray().apply {
            devices.forEach { device ->
                val (encryptedId, iv) = encrypt(device.id)
                put(
                    JSONObject().apply {
                        put("id", encryptedId)
                        put("iv", Base64.encodeToString(iv, Base64.DEFAULT))
                        put("name", device.name)
                    }
                )
            }
        }

        prefs.edit().putString(KEY_DEVICES, array.toString()).apply()
    }

    // --- Encryption / Decryption ---
    private fun encrypt(plainText: String): Pair<String, ByteArray> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12).apply { Random.nextBytes(this) }
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        val encrypted = cipher.doFinal(plainText.toByteArray(charset))
        return Base64.encodeToString(encrypted, Base64.DEFAULT) to iv
    }

    private fun decrypt(encryptedText: String, iv: ByteArray): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        val decoded = Base64.decode(encryptedText, Base64.DEFAULT)
        return String(cipher.doFinal(decoded), charset)
    }

    // --- KeyStore ---
    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        keyStore.getKey(keyAlias, null)?.let { return it as SecretKey }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    companion object {
        private const val PREFS_NAME = "paired_device"
        private const val KEY_DEVICES = "devices"
    }
}
