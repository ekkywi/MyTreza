package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WalletDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("color")
    val color: String?,

    @SerializedName("icon")
    val icon: String?,

    @SerializedName("balance")
    val balance: Double,

    @SerializedName("isArchived")
    val isArchived: Boolean,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)