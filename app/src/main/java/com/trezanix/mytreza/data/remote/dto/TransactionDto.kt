package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TransactionDto(
    val id: String,
    val amount: Double,
    val type: String,
    val description: String? = null,
    val date: String,
    val category:  CategoryDto? = null,
    val wallet: WalletDto? = null,
    val walletId: String,
    val categoryId: String? = null
)

data class CreateTransactionRequest(
    @SerializedName("walletId") val walletId: String,
    @SerializedName("categoryId") val categoryId: String?,
    @SerializedName("type") val type: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("description") val description: String?,
    @SerializedName("date") val date: String
)

data class CreateTransferRequest(
    @SerializedName("fromWalletId") val fromWalletId: String,
    @SerializedName("toWalletId") val toWalletId: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("adminFee") val adminFee: Double = 0.0,
    @SerializedName("description") val description: String?,
    @SerializedName("date") val date: String
)

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