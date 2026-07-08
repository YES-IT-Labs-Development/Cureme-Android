package com.bussiness.curemegptapp.ui.screen.main.medication

import android.net.Uri

fun getFileName(uri: Uri): String {
    return uri.lastPathSegment?.substringAfterLast("/") ?: "File"
}
fun formatTimeInput(input: String): String {
    val digitsOnly = input.filter { it.isDigit() }

    return when {
        digitsOnly.length <= 2 -> digitsOnly
        digitsOnly.length <= 4 -> digitsOnly.take(2) + ":" + digitsOnly.drop(2)
        else -> {
            val hours = digitsOnly.take(2).padStart(2, '0')
            val minutes = digitsOnly.drop(2).take(2).padStart(2, '0')
            val seconds = digitsOnly.drop(4).take(2).padStart(2, '0')
            "$hours:$minutes:$seconds"
        }
    }.take(8) // Max length for HH:MM:SS
}
