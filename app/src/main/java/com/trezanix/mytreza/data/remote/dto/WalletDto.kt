package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.trezanix.mytreza.domain.model.Wallet

data class WalletDto(
    val id: String,
    val name: String,
    val type: String,
    @SerializedName("balance")
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
            accountNumber = "-",
            color = color,
            icon = icon
        )
    }
}

data class WalletDataResponse(
    @SerializedName("items")
    val items: List<WalletDto> = emptyList()
)

data class CreateWalletRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("balance")
    val balance: Double,
    @SerializedName("color")
    val color: String?,
    @SerializedName("icon")
    val icon: String?
)

data class UpdateWalletRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("color")
    val color: String?,
    @SerializedName("icon")
    val icon: String?
)