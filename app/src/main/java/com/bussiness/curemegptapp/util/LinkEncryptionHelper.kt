package com.bussiness.curemegptapp.util

import android.util.Base64

object LinkEncryptionHelper {

    private const val CIPHER_KEY: Byte = 0x5A

    /**
     * Encrypt integer ID into URL-safe token
     */
    fun encrypt(id: Int): String {

        val bytes = id.toString().toByteArray(Charsets.UTF_8)

        val encrypted = ByteArray(bytes.size)

        bytes.forEachIndexed { index, byte ->
            encrypted[index] = (byte.toInt() xor CIPHER_KEY.toInt()).toByte()
        }

        return Base64.encodeToString(
            encrypted,
            Base64.NO_WRAP or Base64.URL_SAFE
        ).trimEnd('=')
    }

    /**
     * Decrypt token back to integer
     * Supports encrypted as well as legacy plain integer values.
     */
    fun decrypt(value: String?): Int? {

        if (value.isNullOrBlank()) return null

        val trimmed = value.trim()

        return try {

            val decoded = Base64.decode(
                trimmed,
                Base64.URL_SAFE or Base64.NO_WRAP
            )

            val decrypted = ByteArray(decoded.size)

            decoded.forEachIndexed { index, byte ->
                decrypted[index] = (byte.toInt() xor CIPHER_KEY.toInt()).toByte()
            }

            String(decrypted, Charsets.UTF_8).toIntOrNull()

        } catch (e: Exception) {

            // Legacy support (plain integer)
            trimmed.toIntOrNull()

        }
    }
}