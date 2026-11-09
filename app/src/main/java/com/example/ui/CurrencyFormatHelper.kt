package com.example.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Formats a raw numeric string into Indonesian thousand-separated Rupiah format with dots.
 * E.g. "50000" -> "50.000", "1500000" -> "1.500.000"
 */
object CurrencyInputFormatter {
    private val symbols = DecimalFormatSymbols(Locale("in", "ID")).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    private val decimalFormat = DecimalFormat("#,###", symbols)

    /**
     * Formats raw digits into dotted thousands, e.g. "50000" -> "50.000".
     * If the input has no digits, returns an empty string.
     */
    fun formatDigits(rawDigits: String): String {
        val clean = rawDigits.filter { it.isDigit() }
        if (clean.isEmpty()) return ""
        val num = clean.toLongOrNull() ?: return clean
        return decimalFormat.format(num)
    }

    /**
     * Returns the formatted string with "Rp " prefix.
     * E.g. "50000" -> "Rp 50.000"
     */
    fun formatWithPrefix(rawDigits: String): String {
        val formatted = formatDigits(rawDigits)
        return if (formatted.isNotEmpty()) "Rp $formatted" else ""
    }

    /**
     * Extracts pure digits as Long from any string (including formatted like "Rp 50.000").
     */
    fun parseToLong(text: String): Long {
        val clean = text.filter { it.isDigit() }
        return clean.toLongOrNull() ?: 0L
    }
}

/**
 * VisualTransformation for Jetpack Compose OutlinedTextField.
 * When the user types raw digits (e.g., "50000"), this transforms the display
 * to dotted thousands without changing the raw model value, while properly
 * mapping cursor offsets.
 */
class RupiahThousandsVisualTransformation : VisualTransformation {
    private val symbols = DecimalFormatSymbols(Locale("in", "ID")).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    private val decimalFormat = DecimalFormat("#,###", symbols)

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val clean = originalText.filter { it.isDigit() }
        if (clean.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val num = clean.toLongOrNull() ?: return TransformedText(text, OffsetMapping.Identity)
        val formatted = decimalFormat.format(num)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, clean.length)
                // Number of grouping separators (dots) before this offset
                val digitsBefore = clamped
                if (digitsBefore == 0) return 0
                val totalDigits = clean.length
                val dotsTotal = (totalDigits - 1) / 3
                val firstGroupLen = totalDigits - dotsTotal * 3
                val dotsBefore = if (digitsBefore <= firstGroupLen) {
                    0
                } else {
                    1 + (digitsBefore - firstGroupLen - 1) / 3
                }
                return (digitsBefore + dotsBefore).coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, formatted.length)
                val transformedSub = formatted.substring(0, clamped)
                val digitsInSub = transformedSub.count { it.isDigit() }
                return digitsInSub.coerceAtMost(clean.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
