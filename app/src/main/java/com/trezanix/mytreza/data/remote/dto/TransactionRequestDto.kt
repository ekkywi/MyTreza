package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateTransactionRequest(
    val walletId: String,
    val amount: Double,
    val type: String,
    val date: String,
    val description: String? = null,
    val categoryId: String? = null
)

data class CreateTransferRequest(
    @SerializedName("fromWalletId")
    val sourceWalletId: String,
    @SerializedName("toWalletId")
    val targetWalletId: String,
    val amount: Double,
    val date: String,
    val description: String? = null,
    val adminFee: Double? = 0.0
)

data class UpdateTransactionRequest(
    val amount: Double,
    val type: String,
    val date: String,
    val description: String? = null,
    val categoryId: String? = null,
    val walletId: String
)