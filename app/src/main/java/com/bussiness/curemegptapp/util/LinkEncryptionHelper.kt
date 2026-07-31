package com.bussiness.curemegptapp.util

import android.net.Uri
import android.util.Base64

object LinkEncryptionHelper {
    private const val CIPHER_KEY: Int = 0x5A

    /** Encrypts an integer ID into a URL-safe encrypted string token */
    fun encrypt(id: Int): String {
        val stringVal = id.toString()
        val bytes = stringVal.toByteArray(Charsets.UTF_8)
        val encryptedBytes = bytes.map { (it.toInt() xor CIPHER_KEY).toByte() }.toByteArray()
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
            .replace("+", "-")
            .replace("/", "_")
            .replace("=", "")
    }

    /** Decrypts a URL-safe encrypted token back into an integer ID */
    fun decrypt(string: String): Int? {
        val trimmed = string.trim()
        if (trimmed.isEmpty()) return null

        var base64 = trimmed
            .replace("-", "+")
            .replace("_", "/")

        val remainder = base64.length % 4
        if (remainder > 0) {
            base64 += "=".repeat(4 - remainder)
        }

        return try {
            val data = Base64.decode(base64, Base64.NO_WRAP)
            val decryptedBytes = data.map { (it.toInt() xor CIPHER_KEY).toByte() }.toByteArray()
            val decryptedString = String(decryptedBytes, Charsets.UTF_8)
            decryptedString.toIntOrNull() ?: trimmed.toIntOrNull()
        } catch (e: Exception) {
            trimmed.toIntOrNull()
        }
    }

    /** Encrypts a string into a URL-safe encrypted string token */
    fun encryptString(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val encryptedBytes = bytes.map { (it.toInt() xor CIPHER_KEY).toByte() }.toByteArray()
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
            .replace("+", "-")
            .replace("/", "_")
            .replace("=", "")
    }

    /** Decrypts a URL-safe encrypted token back into a string */
    fun decryptString(string: String): String? {
        val trimmed = string.trim()
        if (trimmed.isEmpty()) return null

        var base64 = trimmed
            .replace("-", "+")
            .replace("_", "/")

        val remainder = base64.length % 4
        if (remainder > 0) {
            base64 += "=".repeat(4 - remainder)
        }

        return try {
            val data = Base64.decode(base64, Base64.NO_WRAP)
            val decryptedBytes = data.map { (it.toInt() xor CIPHER_KEY).toByte() }.toByteArray()
            val decryptedString = String(decryptedBytes, Charsets.UTF_8)
            decryptedString.ifEmpty { null }
        } catch (e: Exception) {
            Uri.decode(string)
        }
    }
}