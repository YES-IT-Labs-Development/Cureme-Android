package com.bussiness.curemegptapp.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
object UriToRequestBody {

    fun uriToRequestBody(context: Context, uri: Uri): RequestBody {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: ByteArray(0)
        return bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
    }
    //        val contentResolver = context.contentResolver
//        val inputStream = contentResolver.openInputStream(uri)
//        val bytes = inputStream?.readBytes() ?: ByteArray(0)
//        val fileName = getFileName(context, uri) ?: "file"
//        val requestFile = bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
//        return MultipartBody.Part.createFormData(partName, fileName,
//            requestFile
//        )
     fun uriToMultipart(context: Context, uri: Uri, partName: String): MultipartBody.Part {
        val bytes: ByteArray
        val fileName: String

        if (uri.scheme == "http" || uri.scheme == "https") {
            val url = java.net.URL(uri.toString())
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connect()
            bytes = connection.inputStream.readBytes()
            connection.disconnect()
            fileName = uri.lastPathSegment ?: "file"
        } else {
            bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: ByteArray(0)
            fileName = getFileName(context, uri) ?: "file"
        }

        val mimeType = when (fileName.substringAfterLast(".").lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "pdf" -> "application/pdf"
            else -> "application/octet-stream"
        }

        val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, fileName, requestFile)

     }

    fun getFileName(context: Context, uri: Uri): String? {
        var name: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }

    fun uriListToMultipartList(
        context: Context,
        uris: List<Uri>,
        partName: String
    ): List<MultipartBody.Part> {

        return uris.map { uri ->
            uriToMultipart(context, uri, partName)
        }
    }

}

//Design an image caching library like glide.
//Design a 1 to 1 Chat application which can also send images, videos.
//Design a App Store.
//Design an e-commerce app’s homepage.
//Design a stock market app’s ticker page. The page which displays the prices of stocks.


