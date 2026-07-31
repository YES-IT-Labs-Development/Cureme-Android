package com.bussiness.curemegptapp.util

import android.util.Base64

object CryptoUtil {

    private const val CIPHER_KEY: Byte = 0x5A


    /**
     * Encrypts an integer ID into a URL-safe encrypted string token.
     */
    fun encrypt(id: Int): String {

        val data = id.toString()
            .toByteArray(Charsets.UTF_8)

        val encrypted = data.map {
            (it xor CIPHER_KEY).toByte()
        }.toByteArray()


        return Base64.encodeToString(
            encrypted,
            Base64.URL_SAFE or
                    Base64.NO_WRAP or
                    Base64.NO_PADDING
        )
    }


    /**
     * Decrypts encrypted token back into Integer.
     * Supports old plain integer values also.
     */
    fun decrypt(value: String): Int? {

        val trimmed = value.trim()

        if (trimmed.isEmpty())
            return null


        return try {

            val decoded = Base64.decode(
                trimmed,
                Base64.URL_SAFE or
                        Base64.NO_WRAP or
                        Base64.NO_PADDING
            )


            val decrypted = decoded.map {
                (it xor CIPHER_KEY).toByte()
            }.toByteArray()


            String(
                decrypted,
                Charsets.UTF_8
            ).toIntOrNull()
                ?: trimmed.toIntOrNull()


        } catch (e: Exception) {

            trimmed.toIntOrNull()

        }
    }



    /**
     * Encrypts String into URL-safe token.
     */
    fun encryptString(value: String): String {

        val encrypted = value
            .toByteArray(Charsets.UTF_8)
            .map {
                (it xor CIPHER_KEY).toByte()
            }
            .toByteArray()


        return Base64.encodeToString(
            encrypted,
            Base64.URL_SAFE or
                    Base64.NO_WRAP or
                    Base64.NO_PADDING
        )
    }



    /**
     * Decrypts encrypted token back into String.
     */
    fun decryptString(value: String): String? {

        val trimmed = value.trim()

        if (trimmed.isEmpty())
            return null


        return try {

            val decoded = Base64.decode(
                trimmed,
                Base64.URL_SAFE or
                        Base64.NO_WRAP or
                        Base64.NO_PADDING
            )


            val decrypted = decoded.map {
                (it xor CIPHER_KEY).toByte()
            }.toByteArray()


            String(
                decrypted,
                Charsets.UTF_8
            )


        } catch (e: Exception) {

            java.net.URLDecoder.decode(
                trimmed,
                "UTF-8"
            )
        }
    }



    /**
     * XOR helper
     */
    private infix fun Byte.xor(other: Byte): Int {
        return this.toInt() xor other.toInt()
    }
}