package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName

// DTO untuk Create
// DTO untuk Create
data class CreateWalletRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("balance")
    val initialBalance: Double,
    @SerializedName("color")
    val color: String,
    @SerializedName("icon")
    val icon: String
)

// DTO untuk Update
data class UpdateWalletRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("icon")
    val icon: String
)