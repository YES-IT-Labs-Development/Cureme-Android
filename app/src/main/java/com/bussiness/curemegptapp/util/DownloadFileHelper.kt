package com.bussiness.curemegptapp.util

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object DownloadFileHelper {

    suspend fun downloadFileToUri(context: Context, fileUrl: String): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                // File name URL se nikalo
                val fileName = fileUrl.substringAfterLast("/")

                // Cache directory mein save karo
                val file = File(context.cacheDir, fileName)

                val input = connection.inputStream
                val output = FileOutputStream(file)

                input.copyTo(output)

                output.flush()
                output.close()
                input.close()

                Uri.fromFile(file)  // ← Ab yeh proper Uri hai
            } catch (e: Exception) {
                Log.e("DownloadFile", "Error: ${e.message}")
                null
            }
        }
    }


}