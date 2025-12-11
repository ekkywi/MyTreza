package com.trezanix.mytreza.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

// 1. DAFTAR PILIHAN ICON (Strict Map)
// Dipakai untuk Grid Picker & Pencarian Cepat
object CategoryIcons {
    val map = mapOf(
        "fastfood" to Icons.Rounded.Fastfood,
        "restaurant" to Icons.Rounded.Restaurant,
        "cafe" to Icons.Rounded.LocalCafe,
        "shopping" to Icons.Rounded.ShoppingBag,
        "cart" to Icons.Rounded.ShoppingCart,
        "car" to Icons.Rounded.DirectionsCar,
        "flight" to Icons.Rounded.Flight,
        "home" to Icons.Rounded.Home,
        "salary" to Icons.Rounded.Work,
        "gift" to Icons.Rounded.CardGiftcard,
        "savings" to Icons.Rounded.Savings, // Pastikan dependency material-icons-extended ada
        "health" to Icons.Rounded.LocalHospital,
        "school" to Icons.Rounded.School,
        "sport" to Icons.Rounded.SportsSoccer,
        "movie" to Icons.Rounded.Movie,
        "receipt" to Icons.Rounded.Receipt,
        "phone" to Icons.Rounded.PhoneAndroid,
        "computer" to Icons.Rounded.Computer,
        "store" to Icons.Rounded.Store,
        "transfer" to Icons.Rounded.SwapHoriz,
        "other" to Icons.Rounded.Category
    )

    // Helper untuk mengambil list key buat UI Grid di Dialog
    val iconKeys = map.keys.toList()
}

// 2. DAFTAR PILIHAN WARNA
// Dipakai untuk Color Picker
object CategoryColors {
    val colors = listOf(
        "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
        "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50",
        "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800",
        "#FF5722", "#795548", "#9E9E9E", "#607D8B", "#000000"
    )
}

// 3. FUNGSI GET ICON (Hybrid Logic: Strict + Fallback)
fun getCategoryIcon(iconName: String?): ImageVector {
    if (iconName == null) return Icons.Rounded.Category

    // LANGKAH A: Cek apakah iconName ada di map strict kita? (Eksplisit)
    val strictIcon = CategoryIcons.map[iconName]
    if (strictIcon != null) {
        return strictIcon
    }

    // LANGKAH B: Jika tidak ketemu (data lama/manual string), pakai logika "Tebak-tebakan"
    val name = iconName.lowercase().trim()

    return when {
        name.contains("food") || name.contains("makan") || name.contains("resto") -> Icons.Rounded.Restaurant
        name.contains("drink") || name.contains("minum") || name.contains("cafe") || name.contains("kopi") -> Icons.Rounded.LocalCafe
        name.contains("shop") || name.contains("belanja") || name.contains("mall") -> Icons.Rounded.ShoppingBag
        name.contains("transport") || name.contains("kendara") || name.contains("car") || name.contains("mobil") || name.contains("gojek") || name.contains("grab") -> Icons.Rounded.DirectionsCar
        name.contains("salary") || name.contains("gaji") || name.contains("income") || name.contains("masuk") || name.contains("work") -> Icons.Rounded.Work
        name.contains("gift") || name.contains("hadiah") || name.contains("kado") -> Icons.Rounded.CardGiftcard
        name.contains("health") || name.contains("sehat") || name.contains("medis") || name.contains("obat") -> Icons.Rounded.LocalHospital
        name.contains("school") || name.contains("sekolah") || name.contains("kuliah") -> Icons.Rounded.School
        name.contains("sport") || name.contains("olahraga") || name.contains("gym") -> Icons.Rounded.SportsSoccer
        name.contains("enter") || name.contains("hibur") || name.contains("movie") || name.contains("film") -> Icons.Rounded.Movie
        name.contains("bill") || name.contains("tagihan") || name.contains("listrik") -> Icons.Rounded.Receipt
        name.contains("transfer") || name.contains("kirim") -> Icons.Rounded.SwapHoriz
        name.contains("topup") || name.contains("isi") -> Icons.Rounded.AccountBalanceWallet
        name.contains("fami") || name.contains("keluar") || name.contains("home") -> Icons.Rounded.Home
        name.contains("travel") || name.contains("libur") || name.contains("wisata") -> Icons.Rounded.Flight
        name.contains("phone") || name.contains("hp") || name.contains("pulsa") -> Icons.Rounded.PhoneAndroid
        else -> Icons.Rounded.Category
    }
}
