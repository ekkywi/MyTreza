package com.trezanix.mytreza.presentation.util

import java.util.Locale

fun mapTypeLabel(type: String): String {
    return when(type.uppercase()) {
        "EWALLET" -> "E-Wallet"
        "SAVING" -> "Tabungan"
        "FAMILY" -> "Keluarga"
        "ASSET" -> "Aset"
        "BANK" -> "Bank"
        "CASH" -> "Cash"
        else -> type.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
}