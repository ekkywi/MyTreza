package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.trezanix.mytreza.domain.model.Wallet

data class WalletDto(
    val id: String,
    val name: String,
    val type: String,
    val balance: Double,
    val color: String? = null,
    val icon: String? = null
) {
    fun toDomain(): Wallet {
        return Wallet(
            id = id,
            name = name,
            type = type,
            balance = balance,
            accountNumber = "-"
        )
    }
}

data class WalletDataResponse(
    @SerializedName("items")
    val items: List<WalletDto> = emptyList()
)

data class CreateWalletRequest(
    val name: String,
    val type: String,
    val balance: Double
)