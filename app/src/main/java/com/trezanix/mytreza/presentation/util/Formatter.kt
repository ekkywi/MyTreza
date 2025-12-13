package com.trezanix.mytreza.presentation.util

import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

fun getGreetingMessage(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 0..10 -> "Selamat Pagi,"
        in 11..14 -> "Selamat Siang,"
        in 15..18 -> "Selamat Sore,"
        else -> "Selamat Malam,"
    }
}

fun formatDateHeader(dateString: String): String {
    try {
        val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val date = parser.parse(dateString) ?: return dateString

        val now = Calendar.getInstance()
        val check = Calendar.getInstance()
        check.time = date

        return when {
            now.get(Calendar.YEAR) == check.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == check.get(Calendar.DAY_OF_YEAR) -> "Hari Ini"

            now.get(Calendar.YEAR) == check.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) - check.get(Calendar.DAY_OF_YEAR) == 1 -> "Kemarin"

            else -> java.text.SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(date)
        }
    } catch (e: Exception) {
        return dateString
    }
}