package com.bussiness.curemegptapp.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.source
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL

object MultipartHelper {

    private val client = OkHttpClient()

    suspend fun preparePart(
        context: Context,
        partName: String,
        uri: Uri
    ): MultipartBody.Part? = withContext(Dispatchers.IO) {

        try {
            val scheme = uri.scheme

            when (scheme) {
                "http", "https" -> {

                    val request = Request.Builder()
                        .url(uri.toString())
                        .build()

                    val response = client.newCall(request).execute()

                    if (!response.isSuccessful) return@withContext null

                    val body = response.body ?: return@withContext null

                    // ✅ FIX: Read fully into memory (prevents "closed" crash)
                    val bytes = body.bytes()

                    val contentType = body.contentType()
                        ?: "application/octet-stream".toMediaType()

                    val fileName = uri.lastPathSegment
                        ?: "file_${System.currentTimeMillis()}"

                    val requestBody = bytes.toRequestBody(contentType)

                    MultipartBody.Part.createFormData(
                        partName,
                        fileName,
                        requestBody
                    )
                }

                // 📁 HANDLE LOCAL FILE
                "content", "file" -> {

                    val resolver = context.contentResolver

                    val inputStream = resolver.openInputStream(uri)
                        ?: return@withContext null

                    // ✅ Safe read
                    val bytes = inputStream.use { it.readBytes() }

                    val mimeType = resolver.getType(uri)
                        ?: "application/octet-stream"

                    val fileName = "local_${System.currentTimeMillis()}"

                    val requestBody = bytes.toRequestBody(mimeType.toMediaType())

                    MultipartBody.Part.createFormData(
                        partName,
                        fileName,
                        requestBody
                    )
                }

                else -> null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
