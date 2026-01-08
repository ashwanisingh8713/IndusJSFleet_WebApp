package com.indusjs.fleet.web.utils

import kotlin.js.Date
import kotlin.math.pow
import kotlin.math.round

/**
 * Date utility functions for DD-MM-YYYY format used by API
 */
object DateUtils {

    /**
     * Format date to DD-MM-YYYY
     */
    fun formatDate(date: Date): String {
        val day = date.getDate().toString().padStart(2, '0')
        val month = (date.getMonth() + 1).toString().padStart(2, '0')
        val year = date.getFullYear()
        return "$day-$month-$year"
    }

    /**
     * Format current date to DD-MM-YYYY
     */
    fun today(): String = formatDate(Date())

    /**
     * Parse DD-MM-YYYY string to Date
     */
    fun parseDate(dateStr: String): Date? {
        return try {
            val parts = dateStr.split("-")
            if (parts.size == 3) {
                val day = parts[0].toInt()
                val month = parts[1].toInt() - 1 // JS months are 0-indexed
                val year = parts[2].toInt()
                Date(year, month, day)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Format time to HH:MM (24-hour)
     */
    fun formatTime(date: Date): String {
        val hours = date.getHours().toString().padStart(2, '0')
        val minutes = date.getMinutes().toString().padStart(2, '0')
        return "$hours:$minutes"
    }

    /**
     * Get current time as HH:MM
     */
    fun currentTime(): String = formatTime(Date())

    /**
     * Format to ISO 8601 for API requests that need it
     */
    fun toIso8601(date: Date): String {
        return date.toISOString()
    }

    /**
     * Create ISO 8601 datetime from date and time strings
     * @param dateStr DD-MM-YYYY format
     * @param timeStr HH:MM format
     */
    fun toIso8601(dateStr: String, timeStr: String): String? {
        return try {
            val dateParts = dateStr.split("-")
            val timeParts = timeStr.split(":")
            if (dateParts.size != 3 || timeParts.size != 2) return null

            val day = dateParts[0].toInt()
            val month = dateParts[1].toInt() - 1
            val year = dateParts[2].toInt()
            val hours = timeParts[0].toInt()
            val minutes = timeParts[1].toInt()

            val date = Date(year, month, day, hours, minutes, 0)
            date.toISOString()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get relative time string (e.g., "2 hours ago", "Yesterday")
     */
    fun getRelativeTime(dateStr: String?): String {
        if (dateStr == null) return ""

        return try {
            val date = Date(dateStr)
            val now = Date()
            val diffMs = now.getTime() - date.getTime()
            val diffMins = (diffMs / 60000).toInt()
            val diffHours = (diffMins / 60)
            val diffDays = (diffHours / 24)

            when {
                diffMins < 1 -> "Just now"
                diffMins < 60 -> "$diffMins min ago"
                diffHours < 24 -> "$diffHours hours ago"
                diffDays == 1 -> "Yesterday"
                diffDays < 7 -> "$diffDays days ago"
                else -> formatDate(date)
            }
        } catch (e: Exception) {
            dateStr
        }
    }

    /**
     * Format duration in minutes to readable string
     */
    fun formatDuration(minutes: Int): String {
        return when {
            minutes < 60 -> "${minutes}m"
            minutes < 1440 -> {
                val hours = minutes / 60
                val mins = minutes % 60
                if (mins > 0) "${hours}h ${mins}m" else "${hours}h"
            }
            else -> {
                val days = minutes / 1440
                val hours = (minutes % 1440) / 60
                if (hours > 0) "${days}d ${hours}h" else "${days}d"
            }
        }
    }

    /**
     * Format distance in km
     */
    fun formatDistance(km: Double?): String {
        if (km == null) return "NA"
        return if (km < 1) {
            "${(km * 1000).toInt()} m"
        } else {
            "${formatDecimal(km, 1)} km"
        }
    }

    /**
     * Format double with specified decimal places
     */
    fun formatDecimal(value: Double, decimals: Int): String {
        val multiplier = 10.0.pow(decimals)
        val rounded = round(value * multiplier) / multiplier
        val str = rounded.toString()
        val parts = str.split(".")
        return if (parts.size == 1) {
            "${parts[0]}.${"0".repeat(decimals)}"
        } else {
            val decimalPart = parts[1].take(decimals).padEnd(decimals, '0')
            "${parts[0]}.$decimalPart"
        }
    }
}

