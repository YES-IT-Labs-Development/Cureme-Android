package com.bussiness.curemegptapp.util

object TimeFormatter {

    private val regex = Regex(
        """^\s*(\d+)\s+(second|seconds|minute|minutes|hour|hours|day|days|week|weeks|month|months|year|years)\s+ago\s*$""",
        RegexOption.IGNORE_CASE
    )

    fun format(time: String?): String {
        if (time.isNullOrBlank()) return ""

        return try {
            val match = regex.find(time.trim()) ?: return time

            val value = match.groupValues[1]
            val unit = match.groupValues[2].lowercase()

            val shortUnit = when (unit) {
                "second", "seconds" -> "s"
                "minute", "minutes" -> "m"
                "hour", "hours" -> "h"
                "day", "days" -> "d"
                "week", "weeks" -> "w"
                "month", "months" -> "mo"
                "year", "years" -> "y"
                else -> return time
            }

            "$value$shortUnit ago"
        } catch (_: Exception) {
            // Never crash the UI because of malformed backend data
            time
        }
    }
}