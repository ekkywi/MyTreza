package com.trezanix.mytreza.domain.model

data class Transaction(
    val id: String,
    val amount: Double,
    val description: String?,
    val date: String,
    val type: String, // INCOME / EXPENSE
    val categoryName: String,
    val walletName: String,
    val categoryId: String? = null,
    val walletId: String? = null
)