package com.bussiness.curemegptapp.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    private val outputFormatter =
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)

    @RequiresApi(Build.VERSION_CODES.O)
    private val inputFormatters = listOf(
        DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH),   // 7/23/2026
        DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.ENGLISH), // 03-26-2026
        DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH), // 26-03-2026
        DateTimeFormatter.ISO_LOCAL_DATE                      // 2026-03-26
    )

    /**
     * Supported formats:
     * 7/23/2026    -> 23 Jul 2026
     * 12/5/2026    -> 05 Dec 2026
     * 03-26-2026   -> 26 Mar 2026
     * 26-03-2026   -> 26 Mar 2026
     * 2026-03-26   -> 26 Mar 2026
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatToDisplay(date: String?): String {
        if (date.isNullOrBlank()) return ""

        inputFormatters.forEach { formatter ->
            try {
                return LocalDate.parse(date, formatter).format(outputFormatter)
            } catch (_: Exception) {
                // Try next formatter
            }
        }

        return date
    }
}