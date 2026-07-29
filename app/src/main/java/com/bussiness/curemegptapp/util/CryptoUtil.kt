package com.bussiness.curemegptapp.util

import android.util.Base64
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtil {

    private const val SECRET_KEY = "CureMe@2026#AppsFlyer"

    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_LENGTH = 12
    private const val TAG_LENGTH = 128

    private val secretKey: SecretKeySpec by lazy {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(SECRET_KEY.toByteArray(StandardCharsets.UTF_8))
        SecretKeySpec(digest, "AES")
    }

    fun encrypt(plainText: String): String {

        val iv = ByteArray(IV_LENGTH)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKey,
            GCMParameterSpec(TAG_LENGTH, iv)
        )

        val encrypted = cipher.doFinal(
            plainText.toByteArray(StandardCharsets.UTF_8)
        )

        val byteBuffer = ByteBuffer.allocate(iv.size + encrypted.size)
        byteBuffer.put(iv)
        byteBuffer.put(encrypted)

        return Base64.encodeToString(
            byteBuffer.array(),
            Base64.URL_SAFE or Base64.NO_WRAP
        )
    }

    fun decrypt(cipherText: String): String {

        val decoded = Base64.decode(
            cipherText,
            Base64.URL_SAFE or Base64.NO_WRAP
        )

        val byteBuffer = ByteBuffer.wrap(decoded)

        val iv = ByteArray(IV_LENGTH)
        byteBuffer.get(iv)

        val encrypted = ByteArray(byteBuffer.remaining())
        byteBuffer.get(encrypted)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            GCMParameterSpec(TAG_LENGTH, iv)
        )

        return String(
            cipher.doFinal(encrypted),
            StandardCharsets.UTF_8
        )
    }
}