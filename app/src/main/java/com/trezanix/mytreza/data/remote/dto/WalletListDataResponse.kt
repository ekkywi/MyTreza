package com.trezanix.mytreza.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WalletListDataResponse(
    @SerializedName("items")
    val items: List<WalletDto>
)