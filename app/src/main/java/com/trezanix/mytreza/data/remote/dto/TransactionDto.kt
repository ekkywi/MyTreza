package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TransactionDto(
    val id: String,
    val amount: Double,
    val type: String,
    val description: String? = null,
    val date: String,
    val category: CategoryDto? = null,
    val wallet: WalletDto? = null, // Pastikan WalletDto ada
    val walletId: String,
    val categoryId: String? = null
)


// Request classes moved to TransactionRequestDto.kt


data class TransactionDataResponse(
    @SerializedName("items")
    val items: List<TransactionDto> = emptyList(),
    @SerializedName("meta")
    val meta: PaginationMeta? = null
)

data class PaginationMeta(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)