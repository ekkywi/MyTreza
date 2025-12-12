package com.trezanix.mytreza.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

// 1. DAFTAR PILIHAN ICON (Strict Map)
// Dipakai untuk Grid Picker & Pencarian Cepat
object CategoryIcons {
    val map = mapOf(
        // Food & Drink
        "fastfood" to Icons.Rounded.Fastfood,
        "restaurant" to Icons.Rounded.Restaurant,
        "cafe" to Icons.Rounded.LocalCafe,
        "bar" to Icons.Rounded.LocalBar,
        "dining" to Icons.Rounded.LocalDining,
        "pizza" to Icons.Rounded.LocalPizza,
        "bakery" to Icons.Rounded.BakeryDining,
        "icecream" to Icons.Rounded.Icecream,
        
        // Shopping & Groceries
        "shopping" to Icons.Rounded.ShoppingBag,
        "cart" to Icons.Rounded.ShoppingCart,
        "store" to Icons.Rounded.Store,
        "grocery" to Icons.Rounded.LocalGroceryStore,
        "gift" to Icons.Rounded.CardGiftcard,
        
        // Transport
        "car" to Icons.Rounded.DirectionsCar,
        "motorcycle" to Icons.Rounded.TwoWheeler,
        "bus" to Icons.Rounded.DirectionsBus,
        "train" to Icons.Rounded.DirectionsSubway,
        "flight" to Icons.Rounded.Flight,
        "taxi" to Icons.Rounded.LocalTaxi,
        "gas" to Icons.Rounded.LocalGasStation,
        "parking" to Icons.Rounded.LocalParking,
        
        // Home & Utilities
        "home" to Icons.Rounded.Home,
        "apartment" to Icons.Rounded.Apartment,
        "electricity" to Icons.Rounded.ElectricBolt,
        "water" to Icons.Rounded.WaterDrop,
        "internet" to Icons.Rounded.Wifi,
        "phone" to Icons.Rounded.PhoneAndroid,
        "tv" to Icons.Rounded.Tv,
        "furniture" to Icons.Rounded.Chair,
        "repair" to Icons.Rounded.Build,
        
        // Finance & Work
        "salary" to Icons.Rounded.Work,
        "savings" to Icons.Rounded.Savings,
        "bank" to Icons.Rounded.AccountBalance,
        "wallet" to Icons.Rounded.AccountBalanceWallet,
        "money" to Icons.Rounded.AttachMoney,
        "investment" to Icons.AutoMirrored.Rounded.TrendingUp,
        "receipt" to Icons.Rounded.Receipt,
        "chart" to Icons.Rounded.InsertChart,
        "credit_card" to Icons.Rounded.CreditCard,

        // Aliases / Seed Data Support
        "medical" to Icons.Rounded.LocalHospital,
        "work" to Icons.Rounded.Work,
        "laptop" to Icons.Rounded.Computer, // Mapping 'laptop' to Computer if Laptop is unavailable
        "sell" to Icons.Rounded.Sell,
        "swap_horiz" to Icons.Rounded.SwapHoriz,
        "send" to Icons.AutoMirrored.Rounded.Send,
        "more_horiz" to Icons.Rounded.MoreHoriz,
        
        // Health & Education
        "health" to Icons.Rounded.LocalHospital,
        "medicine" to Icons.Rounded.Medication,
        "doctor" to Icons.Rounded.Person,
        "school" to Icons.Rounded.School,
        "book" to Icons.AutoMirrored.Rounded.MenuBook,
        "science" to Icons.Rounded.Science,
        
        // Entertainment & Leisure
        "movie" to Icons.Rounded.Movie,
        "music" to Icons.Rounded.MusicNote,
        "game" to Icons.Rounded.SportsEsports,
        "ticket" to Icons.Rounded.LocalActivity,
        "camera" to Icons.Rounded.PhotoCamera,
        "park" to Icons.Rounded.Park,
        "beach" to Icons.Rounded.BeachAccess,
        "hotel" to Icons.Rounded.Hotel,
        
        // Sports
        "sport" to Icons.Rounded.SportsSoccer,
        "gym" to Icons.Rounded.FitnessCenter,
        "pool" to Icons.Rounded.Pool,
        "bike" to Icons.Rounded.PedalBike,
        "run" to Icons.AutoMirrored.Rounded.DirectionsRun,
        
        // Electronics
        "computer" to Icons.Rounded.Computer,
        "smartphone" to Icons.Rounded.Smartphone,
        "watch" to Icons.Rounded.Watch,
        "headset" to Icons.Rounded.Headset,
        
        // Others
        "family" to Icons.Rounded.FamilyRestroom,
        "pet" to Icons.Rounded.Pets,
        "baby" to Icons.Rounded.ChildCare,
        "clothes" to Icons.Rounded.Checkroom,
        "laundry" to Icons.Rounded.LocalLaundryService,
        "more_horiz" to Icons.Rounded.MoreHoriz,
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

// 3. FUNGSI GET ICON (Strict Logic Only)
fun getCategoryIcon(iconName: String?): ImageVector {
    if (iconName == null) return Icons.Rounded.Category
    
    // Strict lookup only. No guessing.
    return CategoryIcons.map[iconName] ?: Icons.Rounded.Category
}
