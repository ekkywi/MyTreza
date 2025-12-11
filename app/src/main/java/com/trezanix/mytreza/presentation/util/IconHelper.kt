package com.trezanix.mytreza.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

fun getCategoryIcon(iconName: String?): ImageVector {
    val name = iconName?.lowercase()?.trim() ?: return Icons.Rounded.Category
    
    return when {
        name.contains("food") || name.contains("makan") || name.contains("resto") || name.contains("fastfood") -> Icons.Rounded.Restaurant
        name.contains("drink") || name.contains("minum") || name.contains("cafe") || name.contains("kopi") -> Icons.Rounded.LocalCafe
        name.contains("shop") || name.contains("belanja") || name.contains("mall") -> Icons.Rounded.ShoppingBag
        name.contains("transport") || name.contains("kendara") || name.contains("car") || name.contains("mobil") || name.contains("gojek") || name.contains("grab") || name.contains("uber") -> Icons.Rounded.DirectionsCar
        name.contains("salary") || name.contains("gaji") || name.contains("income") || name.contains("masuk") || name.contains("work") || name.contains("kerja") -> Icons.Rounded.Work
        name.contains("gift") || name.contains("hadiah") || name.contains("kado") || name.contains("bonus") -> Icons.Rounded.CardGiftcard
        name.contains("health") || name.contains("sehat") || name.contains("medis") || name.contains("obat") || name.contains("medical") -> Icons.Rounded.LocalHospital
        name.contains("school") || name.contains("sekolah") || name.contains("educa") || name.contains("kuliah") || name.contains("pendidikan") -> Icons.Rounded.School
        name.contains("sport") || name.contains("olahraga") || name.contains("gym") || name.contains("lari") -> Icons.Rounded.SportsSoccer
        name.contains("enter") || name.contains("hibur") || name.contains("movie") || name.contains("film") || name.contains("game") -> Icons.Rounded.Movie
        name.contains("bill") || name.contains("tagihan") || name.contains("listrik") || name.contains("air") || name.contains("receipt") -> Icons.Rounded.Receipt
        name.contains("transfer") || name.contains("kirim") || name.contains("swap") || name.contains("send") -> Icons.Rounded.SwapHoriz
        name.contains("topup") || name.contains("isi") || name.contains("depo") -> Icons.Rounded.AccountBalanceWallet
        name.contains("fami") || name.contains("keluar") || name.contains("rumah tangga") || name.contains("home") -> Icons.Rounded.Home
        name.contains("travel") || name.contains("libur") || name.contains("wisata") || name.contains("hotel") -> Icons.Rounded.Flight
        name.contains("grocer") || name.contains("pasar") || name.contains("sayur") -> Icons.Rounded.ShoppingCart
        name.contains("net") || name.contains("wifi") || name.contains("data") || name.contains("laptop") || name.contains("freelance") -> Icons.Rounded.Computer
        name.contains("phone") || name.contains("hp") || name.contains("telp") || name.contains("pulsa") -> Icons.Rounded.PhoneAndroid
        name.contains("sell") || name.contains("jual") -> Icons.Rounded.Store
        else -> Icons.Rounded.Category
    }
}
