package com.bussiness.curemegptapp.util

import java.text.SimpleDateFormat
import java.util.Locale

object CommonUtils {

    fun convertTo24HourFormat(time12: String): String {
        val inputFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.US)
        val date = inputFormat.parse(time12)
        return outputFormat.format(date)
    }


    fun splitValueUnit(input: String, defaultUnit: String): Pair<String, String> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Pair("", defaultUnit)
        }

        val numericRegex = Regex("""^(\d+(?:\.\d+)?)\s*(.*)$""")
        val matchResult = numericRegex.find(trimmed)
        if (matchResult == null) {
            return Pair(trimmed, defaultUnit)
        }

        val value = matchResult.groupValues[1]
        val remaining = matchResult.groupValues[2].trim()

        if (remaining.isEmpty()) {
            return Pair(value, defaultUnit)
        }

        val words = remaining.split(Regex("""\s+"""))
        val firstWord = words.firstOrNull() ?: ""
        val lowerWord = firstWord.lowercase()

        val cleanedUnit = when {
            lowerWord.startsWith("kg") -> "Kg"
            lowerWord.startsWith("lb") -> "Lbs"
            lowerWord.startsWith("cm") -> "Cm"
            lowerWord.startsWith("ft") || lowerWord.startsWith("fe") || lowerWord.startsWith("fo") -> "Feet"
            else -> firstWord
        }

        return Pair(value, cleanedUnit)
    }

    fun cleanMeasurement(input: String?, defaultUnit: String): String {
        if (input.isNullOrBlank()) return "--"
        val (valStr, unitStr) = splitValueUnit(input, defaultUnit)
        if (valStr.isEmpty() || valStr == "--" || valStr == "N/A") {
            return "--"
        }
        return "$valStr $unitStr"
    }

    fun convertToAmPm(time24: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        val date = inputFormat.parse(time24)
        return outputFormat.format(date!!)
    }




}