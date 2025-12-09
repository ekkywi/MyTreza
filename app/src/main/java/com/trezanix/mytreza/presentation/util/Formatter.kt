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