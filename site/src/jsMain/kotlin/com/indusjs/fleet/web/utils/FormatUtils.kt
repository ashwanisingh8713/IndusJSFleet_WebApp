package com.indusjs.fleet.web.utils

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

/**
 * Currency and number formatting utilities
 */
object FormatUtils {

    /**
     * Format amount as Indian Rupees
     */
    fun formatCurrency(amount: Double?): String {
        if (amount == null) return "₹0"
        return "₹${formatNumber(amount)}"
    }

    /**
     * Format number with Indian numbering system (lakhs, crores)
     */
    fun formatNumber(value: Double): String {
        val absValue = abs(value)
        val prefix = if (value < 0) "-" else ""

        return when {
            absValue >= 10000000 -> "${prefix}${formatDecimal(absValue / 10000000, 2)} Cr"
            absValue >= 100000 -> "${prefix}${formatDecimal(absValue / 100000, 2)} L"
            absValue >= 1000 -> "${prefix}${formatDecimal(absValue / 1000, 2)} K"
            else -> "${prefix}${formatDecimal(absValue, 2)}"
        }
    }

    /**
     * Format weight with unit
     */
    fun formatWeight(weight: Double?, unit: String? = "ton"): String {
        if (weight == null) return "NA"
        return "${formatDecimal(weight, 2)} ${unit ?: "ton"}"
    }

    /**
     * Format distance in kilometers
     */
    fun formatDistance(km: Double?): String {
        if (km == null) return "NA"
        return "${formatDecimal(km, 1)} km"
    }

    /**
     * Format fuel quantity
     */
    fun formatFuel(liters: Double?): String {
        if (liters == null) return "NA"
        return "${formatDecimal(liters, 2)} L"
    }

    /**
     * Format percentage
     */
    fun formatPercentage(value: Double?): String {
        if (value == null) return "NA"
        return "${formatDecimal(value, 1)}%"
    }

    /**
     * Format phone number for display
     */
    fun formatPhoneNumber(phone: String?): String {
        if (phone == null) return ""
        // Remove country code prefix if present for display
        return phone.removePrefix("+91").removePrefix("91")
            .chunked(4).joinToString(" ")
    }

    /**
     * Truncate text with ellipsis
     */
    fun truncate(text: String?, maxLength: Int = 50): String {
        if (text == null) return ""
        return if (text.length > maxLength) {
            text.take(maxLength - 3) + "..."
        } else text
    }

    /**
     * Capitalize first letter of each word
     */
    fun titleCase(text: String?): String {
        if (text == null) return ""
        return text.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Format status label for display
     */
    fun formatStatus(status: String?): String {
        if (status == null) return ""
        return status.replace("_", " ").let { titleCase(it) }
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

